package org.diceresearch.opalwebservices.utility.elasticsearch;

import org.diceresearch.opalwebservices.model.dto.DataSetDTO;
import org.diceresearch.opalwebservices.model.dto.DataSetLongViewDTO;
import org.diceresearch.opalwebservices.model.dto.FilterDTO;
import org.diceresearch.opalwebservices.model.dto.FilterValueDTO;
import org.diceresearch.opalwebservices.utility.DataProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("elasticsearch")
@Component
public class ElasticSearchProvider implements DataProvider {
    @Override
    public long getNumberOfDatasets(String searchKey, String[] searchIn, String orderBy, FilterDTO[] filters) {
        return 0; //Todo complete it
    }

    @Override
    public List<DataSetLongViewDTO> getSubListOFDataSets(String searchKey, Long low, Long limit, String[] searchIn, String orderBy, FilterDTO[] filters) {
        return null; //Todo complete it
    }

    @Override
    public List<FilterDTO> getFilters(String searchKey, String[] searchIn) {
        return null; //Todo complete it
    }

    @Override
    public Long getCountOfFilterValue(String filterUri, String valueUri, String searchKey, String[] searchIn) {
        return null;
    }

    @Override
    public DataSetDTO getDataSet(String uri) {
        return null;
    }

    @Override
    public FilterDTO getTopFilterOptions(String filterType, String searchKey, String[] searchIn, String filterText) {
        return null;
    }
}
