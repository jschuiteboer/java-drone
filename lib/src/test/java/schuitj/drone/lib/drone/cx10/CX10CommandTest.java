package schuitj.drone.lib.drone.cx10;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CX10CommandTest {
    @Parameters(name = "{0}")
    public static Object[][] buildParameters() {
        final CX10Command neutral;
        neutral = new CX10Command();

        final CX10Command maxThrottle;
        maxThrottle = new CX10Command();
        maxThrottle.setThrottle(1);

        final CX10Command minThrottle;
        minThrottle = new CX10Command();
        minThrottle.setThrottle(-1);

        final CX10Command maxYaw;
        maxYaw = new CX10Command();
        maxYaw.setYaw(1);

        final CX10Command minYaw;
        minYaw = new CX10Command();
        minYaw.setYaw(-1);

        final CX10Command maxPitch;
        maxPitch = new CX10Command();
        maxPitch.setPitch(1);

        final CX10Command minPitch;
        minPitch = new CX10Command();
        minPitch.setPitch(-1);

        final CX10Command maxRoll;
        maxRoll = new CX10Command();
        maxRoll.setRoll(1);

        final CX10Command minRoll;
        minRoll = new CX10Command();
        minRoll.setRoll(-1);

        final CX10Command takingOff;
        takingOff = new CX10Command();
        takingOff.setTakeOff(true);

        final CX10Command landing;
        landing = new CX10Command();
        landing.setLand(true);

        final CX10Command landingAndTakingOff;
        landingAndTakingOff = new CX10Command();
        landingAndTakingOff.setLand(true);
        landingAndTakingOff.setTakeOff(true);
        
        return new Object[][]{
            { neutral,             new byte[] { -52, -128, -128, -128, -128, 0, 0, 51 }, },
            { maxThrottle,         new byte[] { -52, -128, -128, -1, -128, 0, 127, 51 }, },
            { minThrottle,         new byte[] { -52, -128, -128, 1, -128, 0, -127, 51 }, },
            { maxYaw,              new byte[] { -52, -128, -128, -128, -1, 0, 127, 51 }, },
            { minYaw,              new byte[] { -52, -128, -128, -128, 1, 0, -127, 51 }, },
            { maxPitch,            new byte[] { -52, -128, -1, -128, -128, 0, 127, 51 }, },
            { minPitch,            new byte[] { -52, -128, 1, -128, -128, 0, -127, 51 }, },
            { maxRoll,             new byte[] { -52, -1, -128, -128, -128, 0, 127, 51 }, },
            { minRoll,             new byte[] { -52, 1, -128, -128, -128, 0, -127, 51 }, },
            { takingOff,           new byte[] { -52, -128, -128, -128, -128, 1, 1, 51 }, },
            { landing,             new byte[] { -52, -128, -128, -128, -128, 2, 2, 51 }, },
            { landingAndTakingOff, new byte[] { -52, -128, -128, -128, -128, 1, 1, 51 }, }
        };
    }

    private final CX10Command subject;
    private final byte[] expectedOutput;

    public CX10CommandTest(CX10Command subject, byte[] expectedOutput) {
        this.subject = subject;
        this.expectedOutput = expectedOutput;
    }

    @Test
    public void testToByteArray() {
        Assert.assertArrayEquals(expectedOutput, subject.toByteArray());
    }
}
