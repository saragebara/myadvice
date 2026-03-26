package com.sad.myadvice.entity;

public enum Major {

    CSG("CSG", "Bachelor of Computer Science (General)"),
    CSH("CSH", "Bachelor of Computer Science (Honours)"),
    CSHAC("CSHAC", "Bachelor of Computer Science (Honours Applied Computing)"),
    CIS("CIS", "Bachelor of Science (Honours Computer Information Systems)"),
    CSSE("CSSE", "Bachelor of Science (Honours CS with Software Engineering)"),
    BACS("BACS", "Bachelor of Commerce (Honours Business Administration and CS)"),
    MCS("MCS", "Bachelor of Mathematics (Honours Mathematics and CS)"),
    BIT("BIT", "Bachelor of Information Technology");

    private final String code;
    private final String fullName;

    Major(String code, String fullName) {
        this.code = code;
        this.fullName = fullName;
    }

    public String getCode() { return code; }
    public String getFullName() { return fullName; }

    //Convert from JSON code string to enum
    public static Major fromCode(String code) {
        for (Major p : values()) {
            if (p.code.equalsIgnoreCase(code.trim())) return p;
        }
        return null;
    }

    @Override
    public String toString() { return fullName; }

    public static Major fromFullName(String fullName) {
        for (Major m : values()) {
            if (m.fullName.equalsIgnoreCase(fullName.trim())) return m;
        }
        return null;
    }
}