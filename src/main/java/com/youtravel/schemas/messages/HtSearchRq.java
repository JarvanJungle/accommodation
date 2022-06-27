package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "HtSearchRq")
@XmlType(propOrder = {
        "langID",
        "success",
        "countries"
})
@Data
public class HtSearchRq {
    private String langID;
    private boolean success;

    private List<Country> countries;

    @XmlElement(name = "LangID")
    public void setLangID(String langID) {
        this.langID = langID;
    }

    @XmlElement(name = "Success")
    public void setSuccess(boolean success) {
        this.success = success;
    }


    @XmlElement(name = "Country")
    public List<Country> getCountries() {
        return countries;
    }

}
