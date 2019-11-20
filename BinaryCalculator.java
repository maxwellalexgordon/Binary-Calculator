/**
 * Class with methods to implement the basic arithmetic operations by operating
 * on bit fields.
 *
 * This is the skeleton code form COMP2691 Assignment #2.
 */
public class BinaryCalculator {

	public static boolean neg(BitField a) {
		return a.getMSB();
	}

	public static BitField shrink(BitField a) {
		BitField temp = new BitField(a.size() / 2);
		temp.setAllFalse();
		for (int i = 0; i < a.size() / 2; i++) {
			temp.set(i, a.get(i));
		}
		return temp;
	}

	public static BitField allign(BitField a) {
		BitField temp = new BitField(a.size() * 2);
		for (int i = temp.size() - 1; i > a.size() - 1; i--) {
			temp.set(i, a.get(i - a.size()));

		}
		return temp;
	}

	public static BitField grow(BitField a) {
		BitField temp = new BitField(a.size() * 2);
		for (int i = 0; i < a.size(); i++) {
			temp.set(i, a.get(i));
		}
		return temp;
	}

	// check
	public static boolean checkPos(BitField a) {
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i)) {
				return true;
			}
		}
		return false;
	}

	public static BitField fillIn(BitField a) {
		BitField temp = new BitField(a.size() + 1);
		for (int i = 0; i < a.size() - 1; i++) {
			temp.set(i, a.get(i));
		}
		return temp;
	}

	public static BitField shiftRight(BitField a) {
		BitField temp = new BitField(a.size());

		for (int i = 0; i < a.size() - 1; i++) {
			temp.set(i, a.get(i + 1));
		}
		return temp;
	}

	public static BitField shiftLeft(BitField a) {
		BitField temp = new BitField(a.size());

		for (int i = a.size() - 1; i > 0; i--) {
			temp.set(i, a.get(i - 1));
		}
		return temp;
	}

	public static boolean[] fullAdder(boolean a, boolean b, boolean carry) {
		boolean[] SC = new boolean[] { false, false };
		if ((a & b) || ((carry & (a ^ b)))) {
			SC[1] = true;
		}
		if ((a ^ b) ^ carry) {
			SC[0] = true;
		}
		return SC;
	}

	public static BitField twoCompliment(BitField subs) {
		BitField temp = new BitField(subs.size());
		for (int i = 0; i < subs.size(); i++) {
			temp.set(i, !subs.get(i));
		}
		// create '1'
		BitField addOne = new BitField(subs.size());
		addOne.setAll(false);
		addOne.set(0, true);

		// add 1
		temp = add(temp, addOne);
		return temp;
	}

	public static BitField add(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.add(a,b): a and b cannot be null and must be the same length.");
		}
		// add code using full adder
		BitField result = new BitField(a.size()); // WHAT WILL BE RETURNED
		boolean[] temp = new boolean[] { false, false }; // RESULT FROM FULL ADDER
		boolean carryOut = false;
		// System.out.println(a.toString());
		// System.out.println(b.toString());
		for (int i = 0; i < a.size(); i++) {
			temp = fullAdder(a.get(i), b.get(i), carryOut);
			result.set(i, temp[0]);
			carryOut = temp[1];

		}

		return result;
	}

	public static BitField subtract(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.add(a,b): a and b cannot be null and must be the same length.");
		}
		BitField result = new BitField(a.size()); // WHAT WILL BE RETURNED
		// System.out.println(b.toString());
		b = twoCompliment(b);
		// System.out.println(b.toString());
		result = add(a, b);
		// System.out.println(result.toString());

		return result;
	}

	public static BitField multiply(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.add(a,b): a and b cannot be null and must be the same length.");
		}
		BitField temp = new BitField(a.size());
		// spacing
		// System.out.println("\n\n\n\n\n");

		/*
		 * //original value System.out.println("Original value");
		 * System.out.println("a: " + a.toLongSigned()); System.out.println("a: " +
		 * a.toString()); System.out.println("b: " + b.toLongSigned());
		 * System.out.println("b: " + b.toString());
		 */

		// check for sign
		boolean sign = false; // start positive
		if (neg(a) || (neg(b))) {
			if (neg(a) & neg(b)) {
				sign = false; // double negative is positive
			} else {
				sign = true; // single negative
			}
		}

		// remove sign
		if (neg(a)) {
			a = twoCompliment(a);

		}
		if (neg(b)) {
			b = twoCompliment(b);
		}

		while (checkPos(a)) {
			if (a.getLSB()) {
				temp = add(temp, b);
			}
			a = shiftRight(a);// shift a to the right 1
			b = shiftLeft(b);// shift b to the left 1

		}

		if (sign) {
			temp = twoCompliment(temp);

		}

		return temp;
	}

	public static BitField[] divide(BitField a, BitField b) {
		if (null == a || null == b || a.size() != b.size()) {
			throw new IllegalArgumentException(
					"BinaryCalculator.add(a,b): a and b cannot be null and must be the same length.");
		}

		BitField[] out = new BitField[2];
		out[0] = new BitField(a.size()); // quotient
		out[1] = new BitField(a.size()); // remainder
		boolean sign = false;
		boolean signR = false;

		// really checking if not zero
		if (!checkPos(b)) {
			return null;
		}

		// check sign
		if (a.getMSB() ^ b.getMSB()) {
			sign = true;
		}

		// remove sign
		if (a.getMSB()) {
			a = twoCompliment(a);
			signR = true;

		}
		if (neg(b)) {
			b = twoCompliment(b);
		}

		// shifting
		BitField divisor = new BitField(b.size() * 2); // divisor
		divisor = allign(b);

		BitField rem = new BitField(a.size() * 2); // remainder
		rem = grow(a);

		// calculations
		for (int i = 0; i <= out[0].size(); i++) {
			rem = subtract(rem, divisor);
			if (!rem.getMSB()) {
				out[0] = shiftLeft(out[0]);
				out[0].set(0, true);
			} else {
				rem = add(rem, divisor);
				out[0] = shiftLeft(out[0]);
				out[0].set(0, false);

			}
			divisor = shiftRight(divisor);
		}
		System.out.println(rem.toString());
		out[1] = shrink(rem);
		System.out.println(out[1].toString());

		if (sign) {
			out[0] = twoCompliment(out[0]);
		}
		if (out[1].getMSB() ^ signR) {
			out[1] = twoCompliment(out[1]);
		}

		return out;
	}

	public static void main(String[] args) {

		System.out.println("Hello world");
		BitField a = new BitField(4, "1001");
		BitField b = new BitField(a.size() * 2);
		b = allign(a);
		System.out.println(a.toString());
		System.out.println(b.toString());
		a = shrink(b);
		System.out.println("SHRINK");
		System.out.println(a.toString());
		System.out.println(b.toString());

		/*
		 * System.out.println("Hello world"); BitField a = new BitField(4, "0111");
		 * BitField b = new BitField(4, "0010"); BitField[] out = new BitField[2]; out =
		 * divide(a, b); System.out.println(a.toIntSigned() + "/" + b.toIntSigned());
		 * System.out.println(out[0].toIntSigned());
		 * System.out.println(out[1].toIntSigned());
		 */

	}

}
