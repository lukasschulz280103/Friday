package com.code_design_camp.client.friday.HeadDisplayClient.Util;


import android.content.Context;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Converts {@link com.google.firebase.firestore.FirebaseFirestore}'s {@link FirebaseFirestoreException#getCode()} to an multilingual information
 */
public class FireStoreCodeInterpreter {
    private FirebaseFirestoreException exception;
    private int code;
    private String message;
    private String exceptionMessage;
    private boolean shouldShowRetryButton = true;
    public FireStoreCodeInterpreter(Context c,FirebaseFirestoreException e){
        code = e.getCode().value();
        exceptionMessage = e.getMessage();
        switch(e.getCode()){
            case CANCELLED:
                message = c.getString(R.string.code_cancelled);
                break;
            case UNKNOWN:
                message = c.getString(R.string.code_unknown);
                break;
            case INVALID_ARGUMENT:
                message = c.getString(R.string.code_invalid_arg);
                break;
            case DEADLINE_EXCEEDED:
                message = c.getString(R.string.code_deadline_exceeded);
                break;
            case NOT_FOUND:
                message = c.getString(R.string.code_not_found);
                break;
            case PERMISSION_DENIED:
                message = c.getString(R.string.code_perm_denied);
                break;
            case RESOURCE_EXHAUSTED:
                message = c.getString(R.string.code_exhausted);
                break;
            case ABORTED:
                message = c.getString(R.string.code_aborted);
                break;
            case INTERNAL:
                message = c.getString(R.string.code_internal);
                shouldShowRetryButton = false;
                break;
            case UNAVAILABLE:
                message = c.getString(R.string.code_unavailable);
                break;
            case UNAUTHENTICATED:
                message = c.getString(R.string.code_unauthenticated);
                break;
        }
    }

    /**
     * get the original {@link FirebaseFirestoreException}. Implemented to generify the codee where this method is used to fully use this class
     * @return the original, in the constructor passed {@link FirebaseFirestoreException}
     */
    public FirebaseFirestoreException getException() {
        return exception;
    }

    /**
     * Get the translated message
     * @return translated {@link FirebaseFirestoreException}'s message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return original {@link FirebaseFirestoreException}'s message
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     *
     * @return get the integer casted error code
     */
    public int getCode() {
        return code;
    }
}
