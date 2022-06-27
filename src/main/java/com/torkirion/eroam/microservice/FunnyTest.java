package com.torkirion.eroam.microservice;

import com.torkirion.eroam.microservice.util.TimeZoneUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FunnyTest {

//    public static void main(String[] args) {
//        char[] one = new char[]{'a', 'b', 'c'};
//        char[] two = new char[]{'d', 'e', 'f'};
//        char[] three = new char[]{'g', 'h', 'i', 'j'};
//        char[][] sets = new char[][]{one, two, three};
//        List<List<Character>> collector = new ArrayList<>();
//        ArrayList<Character> combo = new ArrayList<>();
//        combinations(collector, sets, 0, combo);
//        System.out.println(collector);
//    }
//
//    private static void combinations(List<List<Character>> collector, char[][] sets, int n, ArrayList<Character> combo) {
//        if (n == sets.length) {
//            collector.add(new ArrayList<>(combo));
//            return;
//        }
//        for (char c : sets[n]) {
//            combo.add(c);
//            combinations(collector, sets, n + 1, combo);
//            combo.remove(combo.size() - 1);
//        }
//    }
//    public static void main(String[] args) {
//        List<Record> recordsList = getRecords();
//
//        List<Record> list = recordsList
//                .stream()
//                .filter(distinctByKeys(Record::getId, Record::getName))
//                .collect(Collectors.toList());
//
//        for(Record record : list) {
//            System.out.println(record);
//        }
//    }
//
//    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
//    {
//        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
//
//        return t ->
//        {
//            final List<?> keys = Arrays.stream(keyExtractors)
//                    .map(ke -> ke.apply(t))
//                    .collect(Collectors.toList());
//
//            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
//        };
//    }
//
//    private static ArrayList<Record> getRecords()
//    {
//        ArrayList<Record> records = new ArrayList<>();
//
//        records.add(new Record(1l, 10l, "record1", "record1@email.com", "India"));
//        records.add(new Record(1l, 20l, "record1", "record1@email.com", "India"));
//        records.add(new Record(1l, 30l, "record1", "record2@email.com", "India"));
//        records.add(new Record(1l, 40l, "record1", "record2@email.com", "India"));
//        records.add(new Record(3l, 50l, "record3", "record3@email.com", "India"));
//
//        return records;
//    }
//
//    @Data
//    private static class Record
//    {
//        private long id;
//        private long count;
//        private String name;
//        private String email;
//        private String location;
//
//        public Record(long id, long count, String name,
//                      String email, String location) {
//            super();
//            this.id = id;
//            this.count = count;
//            this.name = name;
//            this.email = email;
//            this.location = location;
//        }
//    }
//    public static void main(String[] args) {
//        int timeZoneOffsetByLongitude = TimeZoneUtil.getTimeZoneOffsetByLongitude(new BigDecimal(-105.0));
//        System.out.println(timeZoneOffsetByLongitude);
//    }
}
