package org;

import com.j_spaces.license.LicenseType;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.text.DateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.security.provider.Sun;

public class LicenseManagerCreator {
	LicenseManager m_LicenseManager;
	static final String DATE_DELIMITER = "~";
	static final String RAW_DATA_DELIMITER = "@";
	static final char REPLACE_CHAR = '$';
	static final String LICENSE_TYPE_DELIMITER = "#";
	static final String LICENSE_VERSION_DELIMITER = "^";
	static final String LICENSE_HOST_DELIMITER = "%";
	static final String WARN_SE_DELIMITER = "|";
	private static Logger _logger = Logger.getLogger("com.gigaspaces.kernel");

	public LicenseManagerCreator() throws NoSuchProviderException,
			NoSuchAlgorithmException {
		Security.addProvider(new Sun());
		this.m_LicenseManager = new LicenseManager();
	}

	public String createLicenseKey(long expirationDate, String raw_data,
			LicenseType type, String version, String host) {
		return createLicenseKey(expirationDate, raw_data, type, version, host,
				false);
	}

	public String createLicenseKey(long expirationDate, String raw_data,
			LicenseType type, String version, String host,
			boolean onlyWarnWhenMembersExceededInStandard) {
		String date = DateFormat.getDateInstance(2, Locale.US).format(
				new Date(expirationDate));

		return createLicenseKey(raw_data, type, version, host, date,
				onlyWarnWhenMembersExceededInStandard);
	}

	public String createLicenseKey(String raw_data, LicenseType type,
			String version, String host,
			boolean onlyWarnWhenMembersExceededInStandard) {
		return createLicenseKey(raw_data, type, version, host, "UNLIMITED",
				onlyWarnWhenMembersExceededInStandard);
	}

	public String createLicenseKey(String raw_data, LicenseType type,
			String version, String host) {
		return createLicenseKey(raw_data, type, version, host, false);
	}

	private String createLicenseKey(String raw_data, LicenseType type,
			String version, String host, String date,
			boolean onlyWarnWhenMembersExceededInStandard) {
		raw_data = raw_data.replace("~".toCharArray()[0], '$');
		raw_data = raw_data.replace("@".toCharArray()[0], '$');

		boolean relaxOnStandard = (onlyWarnWhenMembersExceededInStandard)
				&& (type == LicenseType.STANDARD);
		if (relaxOnStandard) {
			String digestData = new String(
					this.m_LicenseManager.getDigestData(date + raw_data + type
							+ version + host
							+ "onlyWarnWhenMembersExceededInStandard"));

			return "\"" + date + "~" + raw_data + "@" + digestData + "#" + type
					+ "^" + version + "%" + host + "|"
					+ "onlyWarnWhenMembersExceededInStandard" + "\"";
		}

		String digestData = new String(this.m_LicenseManager.getDigestData(date
				+ raw_data + type + version + host));

		return "\"" + date + "~" + raw_data + "@" + digestData + "#" + type
				+ "^" + version + "%" + host + "\"";
	}

	public static void main(String[] args) {
		try {
			LicenseManagerCreator manager = new LicenseManagerCreator();
			// if (args.length == 6) {
			// if (args[0].startsWith("-d")) {
			String companyName = "A";
			LicenseType type = LicenseType.COMMUNITY;
			String version = "6.0XAPCommunity";
			String host = "UNBOUND";

			long days = 10000 * 24L * 60L * 60L * 1000L;

			String licenseKey = days > 0L ? manager.createLicenseKey(
					System.currentTimeMillis() + days, companyName, type,
					version, host) : manager.createLicenseKey(companyName,
					type, version, host);

			if (_logger.isLoggable(Level.INFO)) {
				_logger.info("license key: " + licenseKey);
			}

			// }

			// } else if (_logger.isLoggable(Level.INFO)) {
			// _logger.info("Usage : \n1.for generating signature -d <duration in days> <company name>  <License Type "
			// + EnumSet.allOf(LicenseType.class)
			// + " > <version [2.0 2.1 ...] <host>\n"
			// + "Specify duration=0 for unlimited duration\n"
			// + "Specify host=UNBOUND for host-independent license\n");
			// }

		} catch (NoSuchProviderException ex) {
			if (_logger.isLoggable(Level.SEVERE)) {
				_logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NoSuchAlgorithmException ex) {
			if (_logger.isLoggable(Level.SEVERE)) {
				_logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	static class LicenseManager {
		MessageDigest m_Message;

		LicenseManager() throws NoSuchProviderException,
				NoSuchAlgorithmException {
			this.m_Message = MessageDigest.getInstance("SHA", "SUN");
		}

		byte[] getDigestData(String data) {
			this.m_Message.reset();
			this.m_Message.update(data.getBytes());
			return byteToAscii(this.m_Message.digest());
		}

		boolean verifySignature(String signature, String data) {
			this.m_Message.reset();
			this.m_Message.update(data.getBytes());
			return MessageDigest.isEqual(signature.getBytes(),
					byteToAscii(this.m_Message.digest()));
		}

		static byte[] byteToAscii(byte[] data) {
			int len = data.length;
			byte[] result = new byte[len];
			double d = 0.09615384615384616D;

			for (int i = 0; i < len; i++) {
				int intVal = Math.abs(data[i]);
				byte type = (byte) Character.getType((char) intVal);
				if ((type != 9) && (type != 1) && (type != 2) && (type != 10)) {
					intVal = (int) (d * intVal + 90.0D - 12.211538461538462D);
				}
				result[i] = (byte) intVal;
			}
			return result;
		}
	}
}