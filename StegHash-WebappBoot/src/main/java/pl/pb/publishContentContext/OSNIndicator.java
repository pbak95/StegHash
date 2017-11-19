package pl.pb.publishContentContext;

import pl.pb.model.OSNAccount;

/**
 * Created by Patryk on 11/12/2017.
 */
public class OSNIndicator {

    private String hashtag;

    private OSNAccount originOsnAccount;

    private OSNAccount destinationOsnAccount;

    private boolean isUsed = false;

    public OSNIndicator(String hashtag, OSNAccount originOsnAccount, OSNAccount destinationAPI) {
        this.hashtag = hashtag;
        this.originOsnAccount = originOsnAccount;
        this.destinationOsnAccount = destinationAPI;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public OSNAccount getOriginOSNAccount() {
        return originOsnAccount;
    }

    public void setOriginOSNAccount(OSNAccount originOsnAccount) {
        this.originOsnAccount = originOsnAccount;
    }

    public OSNAccount getDestinationOSNAccount() {
        return destinationOsnAccount;
    }

    public void setDestinationOSNAccount(OSNAccount destinationOsnAccount) {
        this.destinationOsnAccount = destinationOsnAccount;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
