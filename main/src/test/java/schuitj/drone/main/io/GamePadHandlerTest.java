package schuitj.drone.main.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GamePadHandlerTest {
    @Parameters(name = "{0}")
    public static Object[][] buildParameters() {
        return new Object[][]{

        };
    }

    private final GamePadHandler subject;

    public GamePadHandlerTest(GamePadHandler subject) {
        this.subject = subject;
    }

    @Test
    public void test() {
        Assert.assertArrayEquals(expectedOutput, subject.toByteArray());
    }
}
