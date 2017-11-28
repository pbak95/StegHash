package pl.pb.publishContentContext;

        import pl.pb.model.OSNAccount;

        import java.util.*;

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

    public OSNIndicator getIndicatorByHashtag(String hashtag, OSNAccount nextAccount) {
        OSNIndicator result = null;
        for (OSNIndicator indicator : osnIndicatorsList) {
            if (nextAccount != null) {
                if (indicator.getHashtag().equals(hashtag) && indicator.isUsed() == false
                        && indicator.getOriginOSNAccount().getClass().equals(nextAccount.getClass())) {
                    indicator.setUsed(true);
                    result = indicator;
                    break;
                }
            } else {
                if (indicator.getHashtag().equals(hashtag) && indicator.isUsed() == false) {
                    indicator.setUsed(true);
                    result = indicator;
                    break;
                }
            }

        }
        return result;
    }

    private List<OSNIndicator> linkHashtagWithAccount(List<String> hashtags, List<OSNAccount> accounts) {
        List<OSNIndicator> indicators = new LinkedList<>();
        Set<OSNAccount> alredyUsedAccounts = new HashSet<>();

        for (int i = 0; i < accounts.size(); i++) {
            OSNAccount originAccount = (i == 0) ? accounts.get(i) :
                    indicators.get(i -1).getDestinationOSNAccount();

            alredyUsedAccounts.add(originAccount);

            if (i == accounts.size() - 1) {
                //last account, so to avoid null, set next indicator to origin from first indicator
                indicators.add(new OSNIndicator(hashtags.get(i),
                        originAccount, indicators.size() > 0 ? indicators.get(0).getOriginOSNAccount() :
                originAccount));
            } else {
                for (int j = 0; j < accounts.size(); j++) {
                    OSNAccount nextOSNAccount = accounts.get(j);
                    if (originAccount.getClass() != nextOSNAccount.getClass()) {
                        if (alredyUsedAccounts.contains(nextOSNAccount)) {
                            nextOSNAccount = findDifferentAccountThisType(nextOSNAccount,
                                    accounts);
                        }
                        indicators.add(new OSNIndicator(hashtags.get(i),
                                originAccount, nextOSNAccount));
                        alredyUsedAccounts.add(nextOSNAccount);
                        break;
                    }
                    if (j == accounts.size() - 1) {
                        OSNAccount nexOSNAccountOfTheSameType = accounts.get(i + 1);
                        if (alredyUsedAccounts.contains(nexOSNAccountOfTheSameType)) {
                            nexOSNAccountOfTheSameType = findDifferentAccountThisType(nexOSNAccountOfTheSameType,
                                    accounts);
                        }
                        indicators.add(new OSNIndicator(hashtags.get(i),
                                originAccount, nexOSNAccountOfTheSameType)); // <- no different osn, so take next
                        alredyUsedAccounts.add(nexOSNAccountOfTheSameType);
                    }
                }
            }
        }
        return indicators;
    }
    private OSNAccount findDifferentAccountThisType(OSNAccount baseAccount,
                                            List<OSNAccount> osnAccounts) {
        OSNAccount result = baseAccount;
        List<OSNAccount> newList = new ArrayList<>();
        newList.addAll(osnAccounts);
        newList.remove(result);
        for (int i = 0; i < newList.size(); i++) {
            OSNAccount anotherAcc = newList.get(i);
            if (anotherAcc.getClass().equals(baseAccount.getClass()) &&
                    anotherAcc.getId() != baseAccount.getId()) {
                return anotherAcc;
            }
        }
        return baseAccount;
    }
}
