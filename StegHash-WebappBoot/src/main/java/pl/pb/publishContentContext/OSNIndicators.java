package pl.pb.publishContentContext;

        import pl.pb.model.OSNAccount;

        import java.util.LinkedList;
        import java.util.List;

/**
 * Created by Patryk on 11/12/2017.
 */
public class OSNIndicators {

    private List<OSNIndicator> osnIndicatorsList;

    public OSNIndicators(List<String> hashtags, List<OSNAccount> accounts) {
        this.osnIndicatorsList = linkHashtagWithAccount(hashtags, accounts);
    }

    public List<OSNIndicator> getOsnIndicatorsList() {
        return osnIndicatorsList;
    }

    public OSNIndicator getIndicatorByHashtag(String hashtag) {
        OSNIndicator result = null;
        for (OSNIndicator indicator : osnIndicatorsList) {
            if (indicator.getHashtag().equals(hashtag)) {
                result = indicator;
                break;
            }
        }
        return result;
    }

    private List<OSNIndicator> linkHashtagWithAccount(List<String> hashtags, List<OSNAccount> accounts) {
        List<OSNIndicator> indicators = new LinkedList<>();
        for (int i = 0; i < accounts.size(); i++) {
            OSNAccount originAccount = accounts.get(i);
            if (i == accounts.size() - 1) {
                //last account, so to avoid null, set next indicator to origin from first indicator
                indicators.add(new OSNIndicator(hashtags.get(i),
                        originAccount, indicators.get(0).getOriginOSNAccount()));
            } else {
                for (int j = i + 1; j < accounts.size(); j++) {
                    OSNAccount nextOSNAccount = accounts.get(j);
                    if (originAccount.getClass() != nextOSNAccount.getClass()) {
                        indicators.add(new OSNIndicator(hashtags.get(i),
                                originAccount, nextOSNAccount));
                        break;
                    }
                    if (j == accounts.size() - 1) {
                        indicators.add(new OSNIndicator(hashtags.get(i),
                                originAccount, accounts.get(i + 1))); //no different osn, so take next
                    }
                }
            }
        }
        return indicators;
    }
}
