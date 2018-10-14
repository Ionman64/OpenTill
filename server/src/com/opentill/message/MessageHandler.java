package com.opentill.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.mail.MailHandler;
import com.opentill.main.Config;
import com.opentill.main.Utils;

public class MessageHandler implements Runnable {
	public void run() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT tbl1.name AS senderName, tbl2.name AS recipientName, tbl2.email, :prefix:tblchat.message FROM :prefix:tblchat INNER JOIN :prefix:operators"
					+ " AS tbl1 ON :prefix:tblchat.sender = tbl1.id INNER JOIN :prefix:operators AS tbl2 ON :prefix:tblchat.recipient = tbl2.id"
					+ " WHERE :prefix:tblchat.created BETWEEN ? AND ? ORDER BY tbl2.email");
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, Utils.getCurrentTimeStamp()-Utils.SECONDS_IN_A_DAY);
			pstmt.setLong(2, Utils.getCurrentTimeStamp());
			rs = pstmt.executeQuery();
			String currentEmail = null;
			String currentName = null;
			StringBuilder currentMessage = new StringBuilder();
			while (rs.next()) {
				if (Utils.isNull(rs.getString(3)) || !Utils.isValidEmail(rs.getString(3))) {
					continue;
				}
				if (Utils.isNull(currentEmail)) {
					currentEmail = rs.getString(3);
					currentName = rs.getString(2);
					currentMessage = new StringBuilder();
				}
				if (!currentEmail.equals(rs.getString(3))) {
					currentMessage.append("Regards,<br>");
					currentMessage.append(Config.APP_NAME);
					Email email = EmailBuilder.startingBlank()
						    .from("OpenTill Robot", Config.emailProperties.getProperty("email_user"))
						    .to(currentName, currentEmail)
						    .withSubject(String.format("%s, you have recieved some messages today that you may have missed", currentName))
						    .withHTMLText(currentMessage.toString())
						    .buildEmail();
					MailHandler.add(email);
					currentMessage = new StringBuilder();
				}
				currentEmail = rs.getString(3);
				currentName = rs.getString(2);
				currentMessage.append(String.format("%s says '%s'<br><br>", rs.getString(1), rs.getString(4)));
			}
			if (currentMessage.length() > 0) {
				currentMessage.append("Regards,<br>");
				currentMessage.append(Config.APP_NAME);
				Email email = EmailBuilder.startingBlank()
					    .from("OpenTill Robot", Config.emailProperties.getProperty("email_user"))
					    .to(currentName, currentEmail)
					    .withSubject(String.format("%s, you have recieved some messages today that you may have missed", currentName))
					    .withHTMLText(currentMessage.toString())
					    .buildEmail();
				MailHandler.add(email);
			}
			} catch (SQLException ex) {
				Log.info(ex.toString());
			} finally {
				DatabaseHandler.closeDBResources(rs, pstmt, conn);
			}
	}
}
