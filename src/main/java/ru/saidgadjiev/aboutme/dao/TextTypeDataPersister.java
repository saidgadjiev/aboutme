package ru.saidgadjiev.aboutme.dao;

import ru.saidgadjiev.ormnext.core.dialect.BaseDialect;
import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.field.datapersister.StringDataPersister;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;

public class TextTypeDataPersister extends StringDataPersister {

    @Override
    public SqlType getOrmNextSqlType() {
        return SqlType.OTHER;
    }

    @Override
    public String getOtherTypeSql(BaseDialect baseDialect, AttributeDefinition def) {
        return "TEXT";
    }
}
