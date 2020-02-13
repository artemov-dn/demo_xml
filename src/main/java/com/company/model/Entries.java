package com.company.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "entries")
public class Entries {
    @JacksonXmlProperty(localName = "entry")
//    @JacksonXmlElementWrapper(useWrapping = true)
//    @JacksonXmlElementWrapper(localName="other_phones")
    private List<Entry> entryList = new ArrayList<>();

    public List<Entry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<Entry> entryList) {
        this.entryList = entryList;
    }
}
