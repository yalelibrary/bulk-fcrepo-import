package edu.yale.library.ladybird.engine.oai;


/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class OaiProvider {

    private String identifier;

    private String url;

    private String bibIdPrefix;

    public OaiProvider(String identifier, String url, String bibIdPrefix) {
        this.identifier = identifier;
        this.url = url;
        this.bibIdPrefix = bibIdPrefix;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUrl() {
        return url;
    }

    public String getBibIdPrefix() {
        return bibIdPrefix;
    }

    @Override
    public String toString() {
        return "OaiProvider{" + "identifier='"
                + identifier + '\''
                + ", url='" + url + '\''
                + ", bibIdPrefix='" + bibIdPrefix + '\''
                + '}';
    }
}
