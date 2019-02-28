package de.upb.cs.dice.opalwebservices.control;

import de.upb.cs.dice.opalwebservices.model.dto.DataSetLongViewDTO;
import de.upb.cs.dice.opalwebservices.model.mapper.ModelToLongViewDTOMapper;
import de.upb.cs.dice.opalwebservices.utility.SparQLRunner;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class RestAPIController {

    private static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final SparQLRunner sparQLRunner;
    private final ModelToLongViewDTOMapper modelToLongViewDTOMapper;


    @Autowired
    public RestAPIController(SparQLRunner sparQLRunner, ModelToLongViewDTOMapper modelToLongViewDTOMapper) {
        this.sparQLRunner = sparQLRunner;
        this.modelToLongViewDTOMapper = modelToLongViewDTOMapper;
    }

    @GetMapping("/dataSets/getNumberOfDataSets")
    public Long getNumberOFDataSets(
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "searchIn", required = false) String[] searchIn,
            @RequestParam(name = "orderBy", required = false) String orderBy, // TODO: 26.02.19 if quality metrics can be set then we need to have asc, des
            @RequestParam(name = "searchFilters", required = false) Map<String, String> filters
            ) {

        Long num = -1L;
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString();

            String query = "SELECT (COUNT(DISTINCT ?s) AS ?num) WHERE { " +
                    "?s a dcat:Dataset. " +
                    "?s ?p ?o. " +
                    "FILTER(isLiteral(?o)). " +
                    "FILTER CONTAINS (?o,\"" + searchQuery + "\"). " +
                    "}";

            pss.setCommandText(query);
            pss.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");

            num = sparQLRunner.execSelectCount(pss.asQuery());
        } catch (Exception e) {
            logger.error("An error occurred in getting the results", e);
        }
        return num;
    }

    @GetMapping("/dataSets/getSubList")
    public List<DataSetLongViewDTO> getSubListOFDataSets(
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "searchIn", required = false) String[] searchIn,
            @RequestParam(name = "orderBy", required = false) String orderBy, // TODO: 26.02.19 if quality metrics can be set then we need to have asc, des
            @RequestParam(name = "searchFilters", required = false) Map<String, String> filters,
            @RequestParam(name = "low", required = false, defaultValue = "0") Long low,
            @RequestParam(name = "limit", required = false, defaultValue = "100") Long limit

    ) {
        List<DataSetLongViewDTO> ret = new ArrayList<>();
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString();

            String query = "SELECT DISTINCT ?s WHERE { GRAPH ?g { " +
                    "?s a dcat:Dataset. " +
                    "?s ?p ?o. " +
                    "FILTER(isLiteral(?o)). " +
                    "FILTER CONTAINS (STR(?o),\"" + searchQuery + "\"). " +
                    "} } OFFSET " + low + " LIMIT " + limit;

            pss.setCommandText(query);
            pss.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");

            List<Resource> dataSets = sparQLRunner.execSelect(pss.asQuery());

            dataSets.forEach(dataSet -> {
                Model model = getGraphOfDataSet(dataSet);
                DataSetLongViewDTO dataSetLongViewDTO = modelToLongViewDTOMapper.toDataSetLongViewDTO(model);
                ret.add(dataSetLongViewDTO);
            });

        } catch (Exception e) {
            logger.error("An error occurred in getting the results", e);
        }
        return ret;
    }

    private Model getGraphOfDataSet(Resource dataSet) {
        Model model;


        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "CONSTRUCT { " + "?dataSet ?predicate ?object .\n" +
                "\t?object ?p2 ?o2}\n" +
                "WHERE { \n" +
                "  GRAPH ?g {\n" +
                "    ?dataSet ?predicate ?object.\n" +
                "    OPTIONAL { ?object ?p2 ?o2 }\n" +
                "  }\n" +
                "}");

        pss.setParam("dataSet", dataSet);

        model = sparQLRunner.executeConstruct(pss.asQuery());

        return model;
    }

}