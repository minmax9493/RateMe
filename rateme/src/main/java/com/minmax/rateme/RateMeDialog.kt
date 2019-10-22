package io.youvr.android.pivo.util.rateme

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.minmax.rateme.R

class RateMeDialog():DialogInterface.OnClickListener{

    private lateinit var mContext:Context
    private val DEFAULT_TITLE:String = "Rate this app"
    private val DEFAULT_TEXT = "How much do you love our app?"
    private val DEFAULT_POSITIVE = "Ok"
    private val DEFAULT_NEGATIVE = "Not Now"
    private val DEFAULT_LATER = "Later"
    private val DEFAULT_THANKS = "Thanks for your feedback!"
    private val DEFAULT_THANKS_DIALOG_MESSAGE = "We're happy that you like Pivo! Please rate us in the Play Store"
    private val DEFAULT_HELP_US = "Help us improve"
    private val DEFAULT_SUBMIT = "Submit"
    private val DEFAULT_CANCEL = "Cancel"
    private val SP_NUM_OF_ACCESS = "numOfAccess"
    private val SP_DISABLED = "disabled"

    private lateinit var alertDialog: AlertDialog
    private lateinit var view: View
    private lateinit var rateMeListener: RateMeListener

    private lateinit var supportEmail:String
    private var forceMode:Boolean = false
    private var title:String? = null
    private var text:String?= null
    private var positiveButtonText:String?=null
    private var negativeButtonText:String?=null
    private var laterButtonText:String?=null
    private var thanksForFeedBackText:String?=null
    private var thanksDialogMessage:String?=null
    private var helpUsMessage:String?=null
    private var submitButtonText:String?=null
    private var cancelButtonText:String?=null
    private var numOfStars:Int = 0
    private var minStar:Int = 4


    constructor(context: Context, supportEmail:String) : this(){
        this.mContext = context
        this.supportEmail = supportEmail
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which){
            DialogInterface.BUTTON_POSITIVE -> openStore()
            DialogInterface.BUTTON_NEUTRAL -> {
                rateMeListener.onLaterClick()
                dissmiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                rateMeListener.onLaterClick()
                dissmiss()
            }
        }
    }

    private fun dissmiss(){
        alertDialog.dismiss()
    }

    private fun build(){
        val alertDialogBuilder=AlertDialog.Builder(ContextThemeWrapper(mContext, R.style.Theme_AppCompat_Light_Dialog_Alert))
        val layoutInflater = LayoutInflater.from(mContext)
        view = layoutInflater.inflate(R.layout.dialog_rate_me, null)

        val dialogTitle = if (title == null) DEFAULT_TITLE else title
        val dialogText = if (text == null) DEFAULT_TEXT else text

        val textView = view.findViewById<TextView>(R.id.text_view)
        val titleView = view.findViewById<TextView>(R.id.title_view)
        val rateBarView = view.findViewById<RatingBar>(R.id.rate_me_view)

        titleView.text = dialogTitle
        textView.text = dialogText

        rateBarView.setOnRatingBarChangeListener { ratingBar, v, b ->
            if (v >= minStar) {
                dissmiss()
                showThanksDialog()
                rateMeListener?.onPositiveReview(ratingBar.rating.toInt())
            }else{
                dissmiss()
                showReportUs()
                rateMeListener?.onNegativeReview(v.toInt())
            }
        }

        alertDialog = alertDialogBuilder
                .setCancelable(false)
                .setView(view)
                .setNeutralButton(if (laterButtonText == null) DEFAULT_LATER else laterButtonText, this)
                .create()
    }

    private fun showThanksDialog(){
        val alertDialogbuilder = AlertDialog.Builder(ContextThemeWrapper(mContext, R.style.Theme_AppCompat_Light_Dialog_Alert))
                .setCancelable(false)
                .setPositiveButton(if (positiveButtonText==null)DEFAULT_POSITIVE else positiveButtonText, this)
                .setNegativeButton(if (negativeButtonText == null) DEFAULT_NEGATIVE else negativeButtonText, this)

        val inflater = LayoutInflater.from(mContext)
        view = inflater.inflate(R.layout.dialog_thanks_rating_me, null)
        val thanksView = view.findViewById<TextView>(R.id.thanks_view)
        thanksView.text = if (thanksDialogMessage == null)DEFAULT_THANKS_DIALOG_MESSAGE else thanksDialogMessage
        alertDialogbuilder.setView(view)
        alertDialog = alertDialogbuilder.create()
        alertDialog.show()
    }

    private fun showReportUs(){
        val alertDialogbuilder = AlertDialog.Builder(ContextThemeWrapper(mContext, R.style.Theme_AppCompat_Light_Dialog_Alert))
                .setCancelable(false)

        val inflater = LayoutInflater.from(mContext)
        view = inflater.inflate(R.layout.dialog_raport_us, null)
        val helpUsView = view.findViewById<TextView>(R.id.help_us_view)
        val reportEditView = view.findViewById<EditText>(R.id.report_edit_view)
        val submitButton = view.findViewById<Button>(R.id.submit_btn)
        val cancelButton = view.findViewById<Button>(R.id.cancel_btn)

        submitButton.setOnClickListener{
            sendEmail()
            dissmiss()
        }

        cancelButton.setOnClickListener {
            dissmiss()
        }

        helpUsView.text = if (helpUsMessage == null)DEFAULT_HELP_US else helpUsMessage
        submitButton.text = if (submitButtonText == null)DEFAULT_SUBMIT else submitButtonText
        cancelButton.text = if (cancelButtonText == null)DEFAULT_CANCEL else cancelButtonText
        alertDialogbuilder.setView(view)
        alertDialog = alertDialogbuilder.create()
        alertDialog.show()
    }

    fun show(){
        build()
        alertDialog.show()
    }

    private fun openStore(){
        val appPackageName = mContext.getPackageName()
        try {
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun sendEmail(){
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/email"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + mContext.getPackageName() + ")")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    fun setHelpUs(helpUs:String):RateMeDialog{
        this.helpUsMessage = helpUs
        return this
    }

    fun setSubmitText(submitText:String):RateMeDialog{
        this.submitButtonText = submitText
        return this
    }

    fun setCancelText(cancelText:String):RateMeDialog{
        this.cancelButtonText = cancelText
        return this
    }

    fun setThanksDialogMessage(thanksForFeedBack: String):RateMeDialog{
        this.thanksDialogMessage = thanksForFeedBack
        return this
    }

    fun setThanksForFeedback(thanksForFeedBack:String):RateMeDialog{
        this.thanksForFeedBackText = thanksForFeedBack
        return this
    }

    fun setPositiveButtonText(positiveButtonText:String):RateMeDialog{
        this.positiveButtonText = positiveButtonText
        return this
    }

    fun setNegativeButtonText(negativeButtonText:String):RateMeDialog{
        this.negativeButtonText = negativeButtonText
        return this
    }

    fun setLaterButtonText(laterButtonText:String):RateMeDialog{
        this.laterButtonText = laterButtonText
        return this
    }

    fun setListener(rateMeListener: RateMeListener):RateMeDialog{
        this.rateMeListener = rateMeListener
        return this
    }

    fun setForceMode(forceMode:Boolean):RateMeDialog{
        this.forceMode = forceMode
        return this
    }

    fun setTitle(title:String):RateMeDialog{
        this.title = title
        return this
    }

    fun setText(text:String):RateMeDialog{
        this.text = text
        return this
    }

    fun setNumOfStars(numberOfStars:Int):RateMeDialog{
        this.numOfStars = numOfStars
        return this
    }

    companion object{
        @JvmStatic fun getInstance(context: Context, supportEmail: String): RateMeDialog {
            return RateMeDialog(context, supportEmail)
        }
    }
}