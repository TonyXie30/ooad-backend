package com.dormitory.backend.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.dormitory.backend.pojo.Gender;
import org.springframework.stereotype.Component;

@Component
public class GenderConverter implements Converter<Gender> {
    @Override
    public Class<Gender> supportJavaTypeKey() {
        return Gender.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Gender convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new Gender(cellData.getStringValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Gender> context) {
        return new WriteCellData<>(context.getValue().getGender());
    }
}
