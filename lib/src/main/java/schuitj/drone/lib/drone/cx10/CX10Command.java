package schuitj.drone.lib.drone.cx10;

import lombok.Data;
import org.apache.commons.lang3.Validate;
import schuitj.drone.lib.drone.net.Command;
import schuitj.drone.lib.util.ByteUtils;

@Data
public class CX10Command implements Command {
    private float pitch;
    private float yaw;
    private float roll;
    private float throttle;
    private boolean takeOff;
    private boolean land;

    public void setPitch(float amount) {
        Validate.inclusiveBetween(-1f, 1f, amount);
        this.pitch = amount;
    }

    public void setYaw(float amount) {
        Validate.inclusiveBetween(-1f, 1f, amount);
        this.yaw = amount;
    }

    public void setRoll(float amount) {
        Validate.inclusiveBetween(-1f, 1f, amount);
        this.roll = amount;
    }

    public void setThrottle(float amount) {
        Validate.inclusiveBetween(-1f, 1f, amount);
        this.throttle = amount;
    }

    @Override
    public byte[] toByteArray() {
        byte pitch    = (byte) (this.getPitch() * 127 + 128);
        byte yaw      = (byte) (this.getYaw() * 127 + 128);
        byte roll     = (byte) (this.getRoll() * 127 + 128);
        byte throttle = (byte) (this.getThrottle() * 127 + 128);
        boolean takeOff = this.isTakeOff();
        boolean land = this.isLand();

        byte[] data = new byte[8];
        data[0] = (byte) 0xCC;
        data[1] = roll;
        data[2] = pitch;
        data[3] = throttle;
        data[4] = yaw;
        data[5] = takeOff ? (byte) 0x01 : (land ? (byte) 0x02 : 0);
        data[6] = createChecksum(ByteUtils.asUnsigned(data[1], data[2], data[3], data[4], data[5]));
        data[7] = (byte) 0x33;

        return data;
    }

    private byte createChecksum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
            sum ^= b;
        }
        return sum;
    }
}
