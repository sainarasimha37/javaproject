package airHockey;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MalletMotionListener extends MouseMotionAdapter implements Serializable {
	private static final long serialVersionUID = 1L;
	private World world;

	public MalletMotionListener(World world) {
		this.world = world;
	}

	// mallet moves with mouse
	@Override
	public void mouseMoved(MouseEvent e) {
		// move your mallet to wherever the mouse pointer is located
		Point point = world.getLocation();
		try {
			world.moveMallet(point);

			// send location of your mallet to second players
			world.sendCommand(world.table.getMalletCommand());
		}
		catch (IOException | LineUnavailableException | UnsupportedAudioFileException | InterruptedException e1) {
			e1.printStackTrace();
		}

		world.repaint();
	}
}
