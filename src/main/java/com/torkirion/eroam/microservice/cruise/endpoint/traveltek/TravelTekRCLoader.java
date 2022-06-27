package com.torkirion.eroam.microservice.cruise.endpoint.traveltek;

import com.google.common.collect.Lists;
import com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data.*;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.traveltek.schemas.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class TravelTekRCLoader {

    private Unmarshaller unmarshaller;
    private final ShipDataRepo shipDataRepo;
    private final RegionDataRepo regionDataRepo;
    private final SidDataRepo sidDataRepo;
    private final TickFlatFileDataRepo tickFlatFileDataRepo;
    private final CruiseDataRepo cruiseDataRepo;
    private final PortDataRepo portDataRepo;
    private final CruiseLineDataRepo cruiseLineDataRepo;
    private final SystemPropertiesDAO propertiesDAO;
    private Set<BigInteger> portIdList;
    public static final String COUNTRY_DEFAULT = "au";

    public TravelTekRCLoader(ShipDataRepo shipDataRepo,
                             RegionDataRepo regionDataRepo,
                             SidDataRepo sidDataRepo,
                             TickFlatFileDataRepo tickFlatFileDataRepo,
                             CruiseDataRepo cruiseDataRepo,
                             PortDataRepo portDataRepo,
                             CruiseLineDataRepo cruiseLineDataRepo,
                             SystemPropertiesDAO propertiesDAO) throws JAXBException {
        this.shipDataRepo = shipDataRepo;
        this.regionDataRepo = regionDataRepo;
        this.sidDataRepo = sidDataRepo;
        this.tickFlatFileDataRepo = tickFlatFileDataRepo;
        this.cruiseDataRepo = cruiseDataRepo;
        this.portDataRepo = portDataRepo;
        this.cruiseLineDataRepo = cruiseLineDataRepo;
        this.propertiesDAO = propertiesDAO;
        init();
    }

    private void init() throws JAXBException {
        log.debug("init::enter");
        var jaxbContext = JAXBContext.newInstance("com.traveltek.schemas.messages");
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    public void loadCruiseLine() throws Exception {
        log.debug("loadCruiseLine::enter");
        var sidProperties = sidDataRepo.findSidDataByCountryCode(COUNTRY_DEFAULT);
        var travelTekInterface = new TravelTekInterface(propertiesDAO, sidProperties, TravelTekService.SITE_DEFAULT, TravelTekService.CHANNEL);
        List<Line> cruiseLineList = travelTekInterface.getCruiseLines();
        List<CruiseLineData> cruiseLineDataList = new ArrayList<>();
        for (var cruiseLine : cruiseLineList) {
            var cruiseLineData = new CruiseLineData(cruiseLine);
            cruiseLineDataList.add(cruiseLineData);
            if (cruiseLineDataList.size() >= 20) {
                cruiseLineDataRepo.saveAll(cruiseLineDataList);
                cruiseLineDataList.clear();
            }
        }
        if (!cruiseLineDataList.isEmpty())
            cruiseLineDataRepo.saveAll(cruiseLineDataList);
        log.debug("loadCruiseLine::saving list of " + cruiseLineList.size() + " CruiseLine");
    }

    public void loadRegions() throws Exception {
        log.debug("loadRegions::enter");
        var sidProperties = sidDataRepo.findSidDataByCountryCode(COUNTRY_DEFAULT);
        var travelTekInterface = new TravelTekInterface(propertiesDAO, sidProperties, TravelTekService.SITE_DEFAULT, TravelTekService.CHANNEL);
        List<Region> regions = travelTekInterface.getCruiseRegions();
        for (Region region : regions) {
            var regionData = new RegionData(region);
            regionDataRepo.save(regionData);
        }
    }

    public void loadShips() throws Exception {
        log.debug("loadShips::start");
        var sidProperties = sidDataRepo.findSidDataByCountryCode(COUNTRY_DEFAULT);
        var travelTekInterface = new TravelTekInterface(propertiesDAO, sidProperties, TravelTekService.SITE_DEFAULT, TravelTekService.CHANNEL);
        List<Line> lines = travelTekInterface.getCruiseLines();
        for (Line line : lines) {
            log.debug("loadShips::processing cruise line " + line.getCode());
            List<BigInteger> listShipCheck = Lists.newArrayList();
            List<ShipData> shipDataList = Lists.newLinkedList();
            for (Ship ship : line.getShips().getShip()) {
                log.debug("loadShips::processing ship " + ship.getId());
                if (!listShipCheck.contains(ship.getId())) {
                    listShipCheck.add(ship.getId());
                    try {
                        log.debug("loadShips::getting content for ship " + ship.getId() + " " + ship.getName());
                        Ship shipDetail = travelTekInterface.getShipContent(ship.getId());
                        var shipData = new ShipData(shipDetail);
                        shipDataList.add(shipData);
                    } catch (Exception e) {
                        log.warn("loadShips::caught exception " + e.toString(), e);
                    }
                }
            }
            log.debug("loadShips::saving list of " + shipDataList.size() + " ships");
            shipDataRepo.saveAll(shipDataList);
        }

    }

    public void loadCruiseFlatFile() throws Exception {
        log.debug("loadCruiseFlatFile::start");
        var travelTekProperties = new TravelTekProperties(propertiesDAO, TravelTekService.SITE_DEFAULT, TravelTekService.CHANNEL);
        var ftpClient = new FTPClient();
        ftpClient.connect(travelTekProperties.ftpHostname);
        ftpClient.enterLocalPassiveMode();
        ftpClient.login(travelTekProperties.ftpUsername, travelTekProperties.ftpPassword);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        Path tempDir = Files.createTempDirectory("traveltek");
        log.debug("loadCruiseFlatFile::tempDir=" + tempDir + ", now listing files");
        List<FTPFile> ftpFileList = filter(ftpClient.listFiles("*_*_*.zip"));
        List<TickFlatFileData> tickFlatFileDataList = tickFlatFileDataRepo.findAll();
        log.debug("loadCruiseFlatFile::retrieving " + ftpFileList.size() + " files");
        for (FTPFile file : ftpFileList) {
            log.debug("loadCruiseFlatFile::retrieving file " + file.getName());
            if (tickFlatFileDataList.stream().anyMatch(tickFlatFileData -> tickFlatFileData.getName().equals(file.getName())))
            {
                log.debug("loadCruiseFlatFile::ignoring file " + file.getName() + ", already processed in tickFlatFileDataList");
                continue;
            }
            var outputStream = new BufferedOutputStream(new FileOutputStream(tempDir + "/" + file.getName()));
            ftpClient.retrieveFile(file.getName(), outputStream);
            outputStream.close();
            ftpClient.getModificationTime(file.getName());
        }
        ftpClient.disconnect();
        log.debug("loadCruiseFlatFile::retrieved all files");

        portIdList = new LinkedHashSet<>();
        perform(tempDir);

        log.info("loadCruiseFlatFile::end, deleting temp files");
        tempDir.toFile().deleteOnExit();
    }

    private void perform(Path targetDir) throws Exception {
        log.debug("perform::targetDir=" + targetDir.toString());
        try (Stream<Path> streamFiles = Files.list(targetDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".zip"))) {
            List<Path> paths = streamFiles.collect(Collectors.toList());
            log.debug("perform::processing " + paths.size() + " paths");
            for (Path path : paths) {
                log.debug("perform::path=" + path.toString());
                var countryCode = path.getFileName().toString().split("_")[0];
                var folderUnzip = Path.of(targetDir + "/" + FilenameUtils.removeExtension(path.getFileName().toString()));
                log.debug("perform::unzipping to " + folderUnzip);
                unzip(folderUnzip, path);
                dataProcess(folderUnzip, countryCode);
                var tickFlatFileData = new TickFlatFileData();
                tickFlatFileData.setName(path.getFileName().toString());
                tickFlatFileData.setCreateDate(new Date());
                tickFlatFileDataRepo.save(tickFlatFileData);
                savePorts();
            }
        } catch (IOException e) {
            log.warn("perform::caught " + e.toString(), e);
        }
    }

    protected void dataProcess(Path pathTarget, String countryCode) {
        log.debug("dataProcess::pathTarget=" + pathTarget.toString() + ", countryCode=" + countryCode);
        try (Stream<Path> streamPaths = Files.walk(pathTarget)) {
            List<File> files = streamPaths.map(Path::toFile).collect(Collectors.toList());
            List<CruiseData> cruiseDataList = Lists.newArrayList();
            log.debug("dataProcess::processing " + files.size() + " files");
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("getcruisecontent")) {
                    var responseStr = fixResponseNamespaces(Files.readString(file.toPath()));
                    var bin = new ByteArrayInputStream(responseStr.getBytes());
                    var response = ((Response) unmarshaller.unmarshal(bin));
                    log.debug("dataProcess::found " + (response == null || response.getResults() == null || response.getResults().getCruise() == null ? 0 : response.getResults().getCruise().size()) + " results in file " + file.getCanonicalPath());
                    if (response != null && response.getResults() != null && !response.getResults().getCruise().isEmpty()) {
                        for ( Cruise cruise : response.getResults().getCruise())
                        {
	                        var cruiseData = new CruiseData(cruise.getCodetocruiseid().intValue(), countryCode, cruise);
	                        log.debug("dataProcess::cruiseData=" + cruiseData);
	                        if ( cruiseData.getCodeToCruiseId() != null)
	                        {
		                        cruiseDataList.add(cruiseData);
		                        if (cruise.getItinerary() != null && cruise.getItinerary().getItem() != null) {
		                            for (var item : cruise.getItinerary().getItem()) {
		                            	try
		                            	{
			                                if (item.getType().equals("port")) {
			                                    portIdList.add(new BigInteger(item.getPortid()));
			                                }
		                            	}
		                                catch (NumberFormatException nfe)
		                                {
		    	                            log.warn("dataProcess::invalid portId '" + item.getPortid() + "' for cruise " + cruiseData.getCodeToCruiseId() );
		                                }
		                            }
		                        }
	                        }
	                        else
	                        {
	                            log.debug("dataProcess::not saving " + file.getCanonicalPath() + ", cruise ID is empty" );
	                        }
                        }
                    }
                }
                if (cruiseDataList.size() >= 20) {
                    log.debug("dataProcess::savingAll:" + cruiseDataList.size() + " items");
                    cruiseDataRepo.saveAll(cruiseDataList);
                    cruiseDataList.clear();
                }
            }
            if (!cruiseDataList.isEmpty())
            {
                log.debug("dataProcess::savingAll:" + cruiseDataList.size() + " items");
                cruiseDataRepo.saveAll(cruiseDataList);
            }
        } catch (IOException | JAXBException e) {
            log.error(pathTarget.toString());
            e.printStackTrace();
            log.error("convertCruise::" + e.getMessage(), e);
        }
    }

    protected void savePorts() throws Exception {
        log.debug("savePorts::enter");
        var sidProperties = sidDataRepo.findSidDataByCountryCode(COUNTRY_DEFAULT);
        var travelTekInterface = new TravelTekInterface(propertiesDAO, sidProperties, TravelTekService.SITE_DEFAULT, TravelTekService.CHANNEL);
        List<PortData> portDataList = new ArrayList<>();
        log.debug("savePorts::processing " + portIdList.size() + " ports");
        for (var portId : portIdList) {
            log.debug("savePorts::loading port " + portId);
            var portInfo = travelTekInterface.getPortInfo(portId);
            if (portInfo != null) {
                var portData = new PortData(portInfo);
                portDataList.add(portData);
                if (portDataList.size() >= 20) {
                    log.debug("savePorts::saveAll");
                    portDataRepo.saveAll(portDataList);
                    portDataList.clear();
                }
            }
        }
        if (!portDataList.isEmpty())
        {
            log.debug("savePorts::saveAll");
            portDataRepo.saveAll(portDataList);
        }
        portIdList.clear();
    }
    
    private String fixResponseNamespaces(String s) {
        if (s == null)
            return "";
        s = s.replace("<response", "<ns2:response xmlns:ns2=\"http://www.traveltek.com/schemas/messages\"");
        s = s.replace("</response>", "</ns2:response>");
        return s;
    }

    private void unzip(Path targetDir, Path sourceZip) throws IOException {
        Path root = targetDir.normalize();
        try (var is = Files.newInputStream(sourceZip);
             var zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                var path = root.resolve(entry.getName()).normalize();
                if (!path.startsWith(root)) {
                    throw new IOException("Invalid ZIP");
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    try (var os = Files.newOutputStream(path)) {
                        var buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    private List<FTPFile> filter(FTPFile[] files) {
        List<FTPFile> ftpFileList = Lists.newArrayList();
        for (FTPFile file : files) {
            var isExist = false;
            for (var i = 0; i < ftpFileList.size(); i++) {
                if (isSamePrefix(ftpFileList.get(i).getName(), file.getName())) {
                    isExist = true;
                    if (ftpFileList.get(i).getTimestamp().compareTo(file.getTimestamp()) < 1) {
                        ftpFileList.set(i, file);
                        break;
                    }
                }

            }
            if (!isExist)
                ftpFileList.add(file);
        }
        return ftpFileList;
    }

    private String getPrefixNameZip(String name) {
        return name.split("_")[0];
    }

    private boolean isSamePrefix(String currentFile, String targetFile) {
        return currentFile.startsWith(getPrefixNameZip(targetFile));
    }

}
