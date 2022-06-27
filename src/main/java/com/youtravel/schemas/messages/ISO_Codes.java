package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlType(propOrder = {
        "code1",
        "code2",
        "code3"
})
public class ISO_Codes {
    String code1;
    String code2;
    String code3;
    @XmlAttribute(name = "Code_1")
    public void setCode1(String code1) {
        this.code1 = code1;
    }
    @XmlAttribute(name = "Code_2")
    public void setCode2(String code2) {
        this.code2 = code2;
    }
    @XmlAttribute(name = "Code_3")
    public void setCode3(String code3) {
        this.code3 = code3;
    }
}
