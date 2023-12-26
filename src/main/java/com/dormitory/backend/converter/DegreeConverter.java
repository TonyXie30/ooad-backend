package com.dormitory.backend.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.dormitory.backend.pojo.Degree;
import org.springframework.stereotype.Component;

@Component
public class DegreeConverter implements Converter<Degree> {
    @Override
    public Class<Degree> supportJavaTypeKey() {
        return Degree.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Degree convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new Degree(cellData.getStringValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Degree> context) {
        return new WriteCellData<>(context.getValue().getDegree());
    }
}
