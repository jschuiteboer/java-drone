package schuitj.drone.lib.drone.cx10;

import lombok.Data;
import schuitj.drone.lib.drone.Command;
import schuitj.drone.lib.util.ByteUtils;

@Data
public class CX10Command implements Command {
    private int pitch;
    private int yaw;
    private int roll;
    private int throttle;
    private boolean takeOff;
    private boolean land;

    @Override
    public byte[] toByteArray() {
        int pitch = this.getPitch() + 128;
        int yaw = this.getYaw() + 128;
        int roll = this.getRoll() + 128;
        int throttle = this.getThrottle() + 128;
        boolean takeOff = this.isTakeOff();
        boolean land = this.isLand();

        byte[] data = new byte[8];
        data[0] = (byte) 0xCC;
        data[1] = (byte) roll;
        data[2] = (byte) pitch;
        data[3] = (byte) throttle;
        data[4] = (byte) yaw;
        data[5] = takeOff ? (byte) 0x01 : (land ? (byte) 0x02 : 0);

        data[6] = createChecksum(ByteUtils.asUnsigned(data[1], data[2], data[3], data[4], data[5]));

        data[7] = (byte) 0x33;
        return data;
    }

    public static final byte createChecksum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
            sum ^= b;
        }
        return sum;
    }
}
