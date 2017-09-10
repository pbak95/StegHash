import org.junit.Assert;
import org.junit.Test;
import pl.pb.engine.HashTagChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;


/**
 * Created by Patryk on 2017-06-27.
 */
public class HashTagChainTest {

    @Test
    public void hashTagChainShouldReturnFactorialOfBaseListSize(){
        List<String> test = new ArrayList<String>(Arrays.asList("#Warsaw", "#home", "#Poland", "#test123"));
        Map<Integer, List<String>> resultChainTest = HashTagChain.generateChainOfHashtags(test);
        Assert.assertEquals("Hashtag chain size should be factorial of 4(number of hash tags) -> 24", 24, resultChainTest.size());
    }

    @Test
    public void hashTagChainShouldReturnFactorialOfBaseListSize2(){
        List<String> test = new ArrayList<String>(Arrays.asList("#Warsaw", "#home", "#Poland", "#test123"));
        Map<Integer, List<String>> resultChainTest = HashTagChain.generateChainOfHashtags(test);
        assertThat(resultChainTest.size(), is(24));
    }
}
