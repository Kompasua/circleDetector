/**
 * This class creates object line segment. This line segment has start and end
 * coordinates, ant its length.
 * 
 * @author kompas
 *
 */
public class LSegment implements Comparable {
	private final Coordinate a;
	private final Coordinate b;
	private final double length;

	/**
	 * Length of created line segment will be calculated and stored with
	 * according formula.
	 * 
	 * @param a first coordinate
	 * @param b second coordinate
	 */
	public LSegment(Coordinate a, Coordinate b) {
		this.a = a;
		this.b = b;
		length = Math.sqrt((Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2)));
	}

	/**
	 * @return the a
	 */
	public Coordinate getA() {
		return a;
	}

	/**
	 * @return the b
	 */
	public Coordinate getB() {
		return b;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		long temp;
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LSegment other = (LSegment) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Line segment [a=" + a + ", b=" + b + ", length=" + length + "]";
	}

	@Override
	public int compareTo(Object arg0) {
		LSegment obj = (LSegment) arg0;
		if (this.length > obj.length) {
			return 1;
		}
		return 0;
	}

}
