package com.dormitory.backend.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.dormitory.backend.pojo.Subject;
import com.dormitory.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class SubjectConverter implements Converter<Subject> {

//    需要这样来完成注入，直接autowire会导致注入时UserService为空。
//    在发生类似情况时可参考这种注入方案。
    private static UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        SubjectConverter.userService = userService;
    }

    @Override
    public Class<Subject> supportJavaTypeKey() {
        return Subject.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Subject convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return userService.getSubject(cellData.getStringValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Subject> context) {
        return new WriteCellData<>(context.getValue().getId());
    }
}
