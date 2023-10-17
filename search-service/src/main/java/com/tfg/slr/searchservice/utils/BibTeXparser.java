package com.tfg.slr.searchservice.utils;

import com.tfg.slr.searchservice.models.Status;
import com.tfg.slr.searchservice.models.Study;
import org.jbibtex.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BibTeXparser {

    public static void parser(String location) throws ParseException, FileNotFoundException {
        Reader inputReader = new FileReader(location);

        BibTeXParser parser = new BibTeXParser();
        CharacterFilterReader filterReader = new CharacterFilterReader(inputReader);
        BibTeXDatabase database = parser.parseFully(filterReader);

        Map<Key,BibTeXEntry> bibTeXEntryMap = database.getEntries();

        Collection<BibTeXEntry> entries = bibTeXEntryMap.values();
        List<Study> studies = new ArrayList<>();

        for(BibTeXEntry entry : entries) {
            Study study = new Study();
            study.setTitle(entry.getField(BibTeXEntry.KEY_TITLE).toUserString());
            study.setAuthor(entry.getField(BibTeXEntry.KEY_AUTHOR).toUserString());
            study.setYear(Integer.parseInt(entry.getField(BibTeXEntry.KEY_YEAR).toUserString()));
            //study.setVenue(entry.getField(BibTeXEntry.KEY));
            study.setSourceURL(entry.getField(BibTeXEntry.KEY_URL).toUserString());
            //study.setPriority
            study.setSelectionStatus(Status.UNCLASSIFIED);
            study.setExtractionStatus(Status.UNCLASSIFIED);
            //study.score
            study.setDOI(entry.getField(BibTeXEntry.KEY_DOI).toUserString());
            studies.add(study);
        }

        System.out.println(studies);
    }
}
