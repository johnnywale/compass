/*
 * Copyright 2004-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.core.converter.basic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.compass.core.config.CompassConfigurable;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.format.Formatter;
import org.compass.core.converter.basic.format.FormatterFactory;
import org.compass.core.converter.basic.format.TextFormatFormatter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;

/**
 * A base class for number based converters. Allows for formatting (if specified) and for default
 * behaviour handling if none is provided.
 *
 * @author kimchy
 */
public abstract class AbstractNumberConverter extends AbstractFormatConverter implements CompassConfigurable {

    public static final String SORTABLE_FORMAT = "sortable";

    private class NumberFormatter implements FormatterFactory {

        private String format;

        private Locale locale;

        public void configure(String format, Locale locale) {
            this.format = format;
            this.locale = locale;
        }

        public Formatter create() {
            if (SORTABLE_FORMAT.equalsIgnoreCase(format)) {
                Formatter formatter = createSortableFormatter();
                if (formatter == null) {
                    throw new ConversionException("This converter [" + getClass().getName() + "] does not support sortable format");
                }
                return formatter;
            }
            NumberFormat numberFormat;
            if (locale != null) {
                numberFormat = NumberFormat.getInstance(locale);
            } else {
                numberFormat = NumberFormat.getInstance();
            }
            ((DecimalFormat) numberFormat).applyPattern(format);
            return new TextFormatFormatter(numberFormat);
        }

    }

    protected FormatterFactory doCreateFormatterFactory() {
        return new AbstractNumberConverter.NumberFormatter();
    }

    protected abstract Object defaultFromString(String str, ResourcePropertyMapping resourcePropertyMapping);

    protected abstract Object fromNumber(Number number);

    protected abstract Formatter createSortableFormatter();

    protected Object doFromString(String str, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) throws ConversionException {
        if (hasFormatter) {
            for (Formatter formatter : formatters) {
                try {
                    return fromNumber((Number) formatter.parse(str));
                } catch (ParseException e) {
                    // do nothing, continue to the next one
                }
            }
            throw new ConversionException("Failed to parse number [" + str + "]");
        } else {
            return defaultFromString(str, resourcePropertyMapping);
        }
    }

    protected String doToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        if (hasFormatter) {
            return formatters[0].format(o);
        } else {
            return defaultToString(o, resourcePropertyMapping, context);
        }
    }

    protected String defaultToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        return super.doToString(o, resourcePropertyMapping, context);
    }
}
