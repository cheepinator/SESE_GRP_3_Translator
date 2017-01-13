package com.sese.translator.service.mapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverter implements AttributeConverter<ZonedDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(ZonedDateTime locDate) {
    	return (locDate == null ? null : Date.valueOf(locDate.toLocalDate()));
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Date sqlDate) {
    	return (sqlDate == null ? null : ZonedDateTime.from(sqlDate.toLocalDate()));
    }
}
