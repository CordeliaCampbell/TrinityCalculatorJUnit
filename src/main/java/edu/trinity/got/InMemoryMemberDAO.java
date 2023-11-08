package edu.trinity.got;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class InMemoryMemberDAO implements MemberDAO {
    private final Collection<Member> allMembers =
            MemberDB.getInstance().getAllMembers();

    @Override
    public Optional<Member> findById(Long id) {
        Member cur = new Member(id,null, null, null, 0.0, null);
        return allMembers.stream().filter(member -> member.compareTo(cur) == 0).findFirst();
    }

    @Override
    public Optional<Member> findByName(String name) {

        Member cur = new Member(null,null, name, null, 0.0, null);
        return allMembers.stream().filter(member -> member.name().equals(name)).findFirst();
    }

    @Override
    public List<Member> findAllByHouse(House house) {

        return allMembers.stream().filter(member -> member.house() == house).toList();
    }

    @Override
    public Collection<Member> getAll() {

        return allMembers;
    }

    /**
     * Find all members whose name starts with S and sort by id (natural sort)
     */
    @Override
    public List<Member> startWithSandSortAlphabetically() {

        return allMembers.stream().filter(member -> member.name().startsWith("S")).sorted().toList();
    }

    /**
     * Final all Lannisters and sort them by name
     */
    @Override
    public List<Member> lannisters_alphabeticallyByName() {

        return allMembers.stream().filter(member -> member.house() == House.LANNISTER).sorted(Comparator.comparing(Member::name)).toList();
    }

    /**
     * Find all members whose salary is less than the given value and sort by house
     */
    @Override
    public List<Member> salaryLessThanAndSortByHouse(double max) {
        return allMembers.stream().filter(member -> member.salary() < max).sorted(Comparator.comparing(Member::house)).toList();
    }

    /**
     * Sort members by House, then by name
     */
    @Override
    public List<Member> sortByHouseNameThenSortByNameDesc() {

        return allMembers.stream().filter(member -> member.house() == member.house()).sorted(Comparator.comparing(Member::name)).toList();
    }

    /**
     * Sort the members of a given House by birthdate
     */
    @Override
    public List<Member> houseByDob(House house) {

        return allMembers.stream().filter(member -> member.house() == house).sorted(Comparator.comparing(Member::dob)).toList();
    }

    /**
     * Find all Kings and sort by name in descending order
     */
    @Override
    public List<Member> kingsByNameDesc() {

        return allMembers.stream().filter(member -> member.title() == Title.KING).sorted(Comparator.comparing(Member::name)).toList();
    }

    /**
     * Find the average salary of all the members
     */
    @Override
    public double averageSalary() {

        return allMembers.stream().collect(averagingDouble(Member::salary));
    }

    /**
     * Get the names of a given house, sorted in natural order
     * (note sort by _names_, not members)
     */
    @Override
    public List<String> namesSorted(House house) {

        return allMembers.stream().filter(member -> member.house() == house).map(member -> member.name()).sorted().toList();
    }

    /**
     * Are any of the salaries greater than 100K?
     */
    @Override
    public boolean salariesGreaterThan(double max) {

        List<Member> earners = allMembers.stream().filter(member -> member.salary() > max).sorted(Comparator.comparing(Member::house)).toList();
        if(earners.size() >= 1){return true;}
        return false;
    }

    /**
     * Are any of the salaries less than 100K?
     * My added method
     */
    @Override
    public boolean salariesLessThan(double max){
        List<Member> earners = allMembers.stream().filter(member -> member.salary() < max).sorted(Comparator.comparing(Member::house)).toList();
        if(earners.size() >= 1){return true;}
        return false;
    }

    /**
     * Are there any members of given house?
     */
    @Override
    public boolean anyMembers(House house) {
        List<Member> people = allMembers.stream().filter(member -> member.house() == house).sorted(Comparator.comparing(Member::house)).toList();
        if(people.size() != 0){return true;}
        return false;
    }

    /**
     * How many members of a given house are there?
     */
    @Override
    public long howMany(House house) {
        List<Member> people = allMembers.stream().filter(member -> member.house() == house).sorted(Comparator.comparing(Member::house)).toList();
        Long mems = (long)people.size();
        return mems;
    }

    /**
     * Return the names of a given house as a comma-separated string
     */
    @Override
    public String houseMemberNames(House house) {
        return allMembers.stream().filter(member -> member.house() == house).map(member->member.name()).collect(Collectors.joining(", "));
    }

    /**
     * Who has the highest salary?
     */
    @Override
    public Optional<Member> highestSalary() {

        return allMembers.stream().max(Comparator.comparing(Member::salary));
    }

    /**
     * Partition members into royalty and non-royalty
     * (note: royalty are KINGs and QUEENs only)
     */
    @Override
    public Map<Boolean, List<Member>> royaltyPartition() {
        Map<Boolean, List<Member>> royals = allMembers.stream()
                .collect(Collectors.partitioningBy(
                        member -> member.title().equals(Title.KING) || member.title().equals(Title.QUEEN)
                ));
        return royals;
    }

    /**
     * Group members into Houses
     */
    @Override
    public Map<House, List<Member>> membersByHouse() {
        Map<House, List<Member>> houses = new HashMap<House, List<Member>>();

        List avoidForLoop = allMembers.stream().map(member -> {

            if(houses.containsKey(member.house())){
                List<Member> cur = new ArrayList<Member>(houses.get(member.house()));
                cur.add(member);
                houses.replace(member.house(),cur);
            }else{
                List<Member> cur = new ArrayList<Member>();
                cur.add(member);
                houses.put(member.house(), cur);
            }
            return member;
        }).toList();
        return houses;
    }

    /**
     * How many members are in each house?
     * (group by house, downstream collector using counting
     */
    @Override
    public Map<House, Long> numberOfMembersByHouse() {
        Map<House, Long> houses = new HashMap<House, Long>();

        List avoidForLoop = allMembers.stream().map(member -> {

            if(houses.containsKey(member.house())){
                houses.replace(member.house(),houses.get(member.house())+1);
            }else{
                houses.put(member.house(), 1L);
            }
            return member;
        }).toList();
        return houses;
    }

    /**
     * Get the max, min, and ave salary for each house
     */
    @Override
    public Map<House, DoubleSummaryStatistics> houseStats() {
        Map<House, DoubleSummaryStatistics> cur = new HashMap<House, DoubleSummaryStatistics>();
        List avoidForLoop = membersByHouse().keySet().stream().map(house -> {
            cur.put(house, membersByHouse().get(house).stream().collect(Collectors.summarizingDouble(Member::salary)));
            return house;
        }).toList();
        return cur;
    }

}
