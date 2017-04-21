/*  Revelandroid - An app for the Revelation Password Manager data.
    Copyright (C) 2013-2015  Juan Carlos Garza

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.fedztech.revelandroid.data;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class RevelationDataBase {
    /**
     * Entries tree
     */
    private List<Entry> entries = null;
    public List<Entry> getEntries()
    {
        return entries;
    }

    /**
     * Represents a field of an entry.
     *
     */
    public static class Field {
        public final String fieldId;
        public final String fieldValue;
        private Field(String id, String value){
            fieldId = id;
            fieldValue = value;
        }
    };

    /**
     * The basic node of the tree.
     * An entry can contain sub entries and so on.
     */
    public static class Entry {
        public String name;
        public String notes;
        public String description;
        public String updated;
        public String type;
        public ArrayList<Field> fields;
        public ArrayList<Entry> entries;

        private Entry() {
            fields = new ArrayList<Field>();
            entries = new ArrayList<Entry>();
        }
    }

    public static int getDataVersion(byte[] rawData) throws IOException, InvalidParameterException {
        if(rawData==null){
            throw new InvalidParameterException("Parameter is empty.");
        }
        if(rawData.length < 12){
            throw new IOException("Insufficient data.");
        }

        if(rawData[0] != 'r' || rawData[1] != 'v' || rawData[2] != 'l' || rawData[3] != 0x00){
            throw new IOException("Invalid header.");
        }

        return (int)rawData[4];
    }

    /**
     * Holds an empty namepsace
     */
    private static final String ns = null;

    /**
     * Given an XmlPullParser, reads the contents of the XML file
     * and generates a tree of entries.
     * @param parser The parser to use to generate the tree
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        if(entries == null){
            entries = new ArrayList<Entry>();
        }
        else{
            entries.clear();
        }

        // We expect the first tag to be "revelationdata"
        // We recursively read entries
        parser.require(XmlPullParser.START_TAG, ns, "revelationdata");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
    }

    /**
     * Recursive function used to generate the tree of entries
     * @param parser the XmlPullParser to parse the XML data
     * @return The Entry root node of the current subtree
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        Entry newEntry = new Entry();
        for(int i = 0; i< parser.getAttributeCount(); i++){
            if(parser.getAttributeName(i).toString().compareTo("type") == 0){
                String type = parser.getAttributeValue(i);
                if(type != null){
                    newEntry.type = type;
                }
                break;
            }
        }

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                String thename = readName(parser);
                if(thename != null){
                    newEntry.name = thename;
                }
            } else if (name.equals("notes")) {
                String notes = readNotes(parser);
                if(notes != null){
                    newEntry.notes = notes;
                }
            } else if (name.equals("description")) {
                String description = readDescription(parser);
                if(description != null){
                    newEntry.description = description;
                }
            } else if (name.equals("updated")) {
                String updated = readUpdated(parser);
                if(updated != null){
                    newEntry.updated = updated;
                }
            } else if (name.equals("field")) {
                Field newField = readField(parser);
                if(newField != null){
                    newEntry.fields.add(newField);
                }
            } else if (name.equals("entry")) {
                Entry newSubEntry = readEntry(parser);
                if(newSubEntry != null){
                    newEntry.entries.add(newSubEntry);
                }
            } else {
                skip(parser);
            }
        }
        return newEntry;
    }

    /**
     * Reads the name of the entry
     * @param parser
     * @return The name of the entry
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return title;
    }

    /**
     * Reads the notes of the entry
     * @param parser
     * @return The notes of the entry
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readNotes(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "notes");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "notes");
        return title;
    }

    /**
     * Reads the description of the entry
     * @param parser
     * @return The description of the entry
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return title;
    }

    /**
     * Reads the update date of the entry
     * @param parser
     * @return The update date of the entry
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readUpdated(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "updated");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "updated");
        return title;
    }

    /**
     * Reads the next field of the entry
     * @param parser
     * @return The next field of the entry
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Field readField(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "field");
        Field retVal = null;
        String fieldId = null;
        String fieldValue = null;
        for(int i = 0; i < parser.getAttributeCount(); i++){
            String attributeName = parser.getAttributeName(i);
            if(attributeName.toString().compareTo("id") == 0){
                fieldId = parser.getAttributeValue(i);
                fieldValue = readText(parser);
                break;
            }
        }
        retVal = new Field(fieldId, fieldValue);
        parser.require(XmlPullParser.END_TAG, ns, "field");
        return retVal;
    }

    /**
     * Helper function to read text
     * @param parser
     * @return The text read
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips the current element
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
