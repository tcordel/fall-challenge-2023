package fr.tcordel.model;

public enum RadarDirection {

	//la créature est en haut à gauche du drone.
	TL(new Vector(-1000, -1000)),
	//la créature est en haut à droite du drone.
	TR(new Vector(+1000, -1000)),
	//la créature est en bas à droite du drone.
	BR(new Vector(+1000, +1000)),
	//la créature est en bas à gauche du drone.
	BL(new Vector(-1000, +1000));

	private final Vector direction;

	RadarDirection(Vector direction) {this.direction = direction;}

	public Vector getDirection() {
		return direction;
	}
}
