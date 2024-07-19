import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

exports.addAdminRole = functions.https.onCall(async (data, context) => {
  // Check request is made by an authenticated user
  if (!context.auth) {
    throw new functions.https.HttpsError(
      "failed-precondition",
      "The function must be called while authenticated.",
    );
  }

  // Get user and add custom claim (admin)
  try {
    const user = await admin.auth().getUserByEmail(data.email);
    await admin.auth().setCustomUserClaims(user.uid, {
      admin: true,
    });
    return {
      message: `Success! ${data.email} has been made an admin.`,
    };
  } catch (error) {
    if (error instanceof Error) {
      return {
        error: error.message,
      };
    } else {
      return {
        error: "An unknown error occurred",
      };
    }
  }
});
