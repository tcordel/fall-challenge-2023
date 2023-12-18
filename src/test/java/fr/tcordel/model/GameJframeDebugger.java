package fr.tcordel.model;

import java.awt.*;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.*;

public class GameJframeDebugger extends JFrame {

	private static final long seed = -8103073722892937000L;

	private final Game game;

	public GameJframeDebugger() throws HeadlessException, NoSuchAlgorithmException, InterruptedException {
		super("Emulating Discovering depth");
		game = GameUtils.initGame(seed);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Game.WIDTH / 10, Game.HEIGHT / 10);
		setVisible(true);
		setLayout(null);
		repaint();
		for (int i = 0; i < Game.MAX_TURNS; i++) {
			Thread.sleep(300L);
			game.performGameUpdate(i);
			repaint();
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InterruptedException {
		GameJframeDebugger gameJframeDebugger = new GameJframeDebugger();
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.setColor(new Color(0, 255, 255, 50));
		g2d.fill(new Rectangle2D.Double(0, 0, Game.HEIGHT / 10, Game.WIDTH / 10));
		BiFunction<Integer,Entity, Shape> rectShape = (diameter, entity) -> new Double(entity.getPos().getX() / 10 - diameter / 2,
			entity.getPos().getY() / 10 - diameter / 2,
			diameter, diameter);
		printEntity(g2d, game.gamePlayers
				.get(0)
				.drones,
			entity -> rectShape.apply(Game.DRONE_HIT_RANGE / 10, entity),
			(entity) -> Color.orange, Game.DRONE_HIT_RANGE / 10);
		printEntity(g2d, game.gamePlayers
				.get(1)
				.drones,
			entity -> rectShape.apply(Game.DRONE_HIT_RANGE / 10, entity),
			(entity) -> Color.MAGENTA, Game.DRONE_HIT_RANGE / 10);

		printEntity(g2d, game.fishes,
			(entity) -> {
				if (entity instanceof Fish fish) {
					return switch (fish.type) {
						case JELLY -> rectShape.apply(Game.FISH_AVOID_RANGE / 10, fish);
						case FISH -> rectShape.apply(Game.FISH_AVOID_RANGE / 10, fish);
						case CRAB -> rectShape.apply(Game.FISH_AVOID_RANGE / 10, fish);
					};
				} else {
					throw new IllegalArgumentException();
				}
			},

			(entity) -> {
				if (entity instanceof Fish fish) {
					return switch (fish.color) {
						case 0 -> Color.PINK;
						case 1 -> Color.YELLOW;
						case 2 -> Color.GREEN;
						default -> Color.BLUE;
					};
				} else {
					throw new IllegalArgumentException();
				}
			}, Game.FISH_AVOID_RANGE / 10);

	}

	private void printEntity(Graphics2D g2d,
		List<? extends Entity> drones,
		Function<Entity, Shape> shapeFunction,
		Function<Entity, Color> colorFunction,
		int diameter) {
		for (Entity entity : drones) {
			g2d.setColor(colorFunction.apply(entity));
			g2d.fill(shapeFunction.apply(entity));
			g2d.drawString(String.valueOf(entity.getId()), (int)(entity.getPos().getX() / 10 - diameter / 2),
				(int)(entity.getPos().getY() / 10 - diameter / 2));
		}
	}
}