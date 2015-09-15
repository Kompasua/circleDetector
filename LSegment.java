import java.util.ArrayList;

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
	
	public ArrayList<LSegment> sortLines(ArrayList<LSegment> lines){
		ArrayList<LSegment> segmentsSorted = new ArrayList<>();
		ArrayList<LSegment> curLines = new ArrayList<>();
		curLines.addAll(lines);
		segmentsSorted.add(curLines.get(0));
		//for (int i = 0; i <= curLines.size(); i++) {
		int i = 0;
		while(curLines.size() > 0){
			//System.out.println("Iteration "+i);
			//System.out.println("Lines size "+ curLines.size());
			//System.out.println(curLines);
			//System.out.println(segmentsSorted);
			LSegment temp = getNextLine(curLines, segmentsSorted.get(segmentsSorted.size()-1));
			curLines.remove(temp);
			segmentsSorted.add(temp);
			i++;
		}
		//segmentsSorted.add(curLines.get(curLines.size()-1));
		//curLines.remove(0);
		//System.out.println("Lines size "+ curLines.size());
		//System.out.println(curLines);
		//System.out.println(segmentsSorted);
		return segmentsSorted;
	}
	
	public LSegment getNextLine(ArrayList<LSegment> lines, LSegment initLine){
		//System.out.println("Init "+initLine.toString());
		//System.out.println(lines);
		for (LSegment line : lines) {
			if ( (initLine.getA().equals(line.getA()) || initLine.getA().equals(line.getB()))
					&& initLine.equals(line)==false){
				return line;
			}
			if ( (initLine.getB().equals(line.getA()) || initLine.getB().equals(line.getB()))
					&& initLine.equals(line)==false){
				return line;
			}
		}
		System.err.println("NULL!");
		return null;
	}

}
