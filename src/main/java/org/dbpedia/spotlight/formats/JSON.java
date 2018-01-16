package org.dbpedia.spotlight.formats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dbpedia.spotlight.common.ArrayAdapterFactory;
import org.dbpedia.spotlight.common.annotation.AnnotationUnit;
import org.dbpedia.spotlight.common.annotation.ResourceItem;
import org.dbpedia.spotlight.common.candidates.CandidateResourceItem;
import org.dbpedia.spotlight.common.candidates.CandidatesUnit;
import org.dbpedia.spotlight.common.candidates.array.CandidateArrayResourceItem;
import org.dbpedia.spotlight.common.candidates.array.CandidatesArrayUnit;

import java.util.List;

import static org.dbpedia.spotlight.common.Prefixes.DBPEDIA_ONTOLOGY;
import static org.dbpedia.spotlight.common.Prefixes.SCHEMA_ONTOLOGY;

public final class JSON {

    private JSON() {

    }

    public static AnnotationUnit toAnnotation(String content) {

        Gson gson = new Gson();

        AnnotationUnit annotationUnit = gson.fromJson(content, AnnotationUnit.class);

        fixPrefixes(annotationUnit.getResources());

        return annotationUnit;
    }

    public static CandidatesArrayUnit toCandidates(String content) {

        Gson gson  = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();

        CandidatesArrayUnit candidatesUnit = new CandidatesArrayUnit();

        try {
            candidatesUnit = gson.fromJson(content, CandidatesArrayUnit.class);
        } catch (Exception e) {
            CandidatesUnit result = gson.fromJson(content, CandidatesUnit.class);
            candidatesUnit.parse(result);
        }

        if (candidatesUnit != null && candidatesUnit.getCandidatesArrayAnnotation().hasSurfaceForms()) {

            candidatesUnit.getCandidatesArrayAnnotation().getSurfaceForms().forEach(surfaceForm -> {
                fixCandidatePrefixes(surfaceForm.getCandidateArrayResourceItem());
            });

        }

        return candidatesUnit;

    }

    private static void fixPrefixes(List<ResourceItem> resources) {
        if (resources != null && !resources.isEmpty()) {
            resources.forEach(resourceItem -> fixPrefixes(resourceItem));
        }
    }

    private static void fixPrefixes(ResourceItem resource) {
        resource.setTypes(fixPrefixes(resource.getTypes()));
    }

    private static void fixCandidatePrefixes(List<CandidateArrayResourceItem> resources) {
        if (resources != null && !resources.isEmpty()) {
            resources.forEach(resourceItem -> fixCandidatePrefixes(resourceItem));
        }
    }

    private static void fixCandidatePrefixes(CandidateArrayResourceItem resource) {
        resource.setTypes(fixPrefixes(resource.getTypes()));
    }

    private static String fixPrefixes(String value) {

        if (value != null && !value.isEmpty()) {
            return value.replace("Http", "http").
                    replace("DBpedia:", DBPEDIA_ONTOLOGY).
                    replace("Schema:", SCHEMA_ONTOLOGY);
        }
        return value;

    }

}
