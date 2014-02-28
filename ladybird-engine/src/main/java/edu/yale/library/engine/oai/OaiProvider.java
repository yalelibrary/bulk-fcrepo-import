package edu.yale.library.engine.oai;

/**
 *  TODO This would be set up from config
 */
public class OaiProvider
{
    private String identifier; //todo if needed
    private String url;
    private String bibIdPrefix;

    public OaiProvider(String identifier, String url, String bibIdPrefix)
    {
        this.identifier = identifier;
        this.url = url;
        this.bibIdPrefix = bibIdPrefix;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getUrl()
    {
        return url;
    }

    public String getBibIdPrefix()
    {
        return bibIdPrefix;
    }
}
