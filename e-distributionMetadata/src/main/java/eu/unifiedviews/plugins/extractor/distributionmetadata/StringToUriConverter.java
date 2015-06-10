package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToUriConverter implements Converter<String, URI> {

    /**
     * 
     */
    private static final long serialVersionUID = 3057559474392176584L;

    @Override
    public URI convertToModel(String value, Class<? extends URI> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        try {
            return new URI(value);
        } catch (URISyntaxException ex) {
            throw new ConversionException("Could not convert '" + value
                    + "' to " + targetType.getName(), ex);
        }
    }

    @Override
    public String convertToPresentation(URI value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Class<URI> getModelType() {
        return URI.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
