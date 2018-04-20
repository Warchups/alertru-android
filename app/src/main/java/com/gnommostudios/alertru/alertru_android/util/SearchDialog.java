package com.gnommostudios.alertru.alertru_android.util;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.gnommostudios.alertru.alertru_android.R;

public class SearchDialog extends AppCompatDialog {

    public Button yes , no;

    private TextView txt;

    private Context context ;

    final SearchDialog dialog;

    public SearchDialog(Context co)
    {
        this.context = co;
        dialog = new SearchDialog(context);
    }

    public void SearchDialog(String message , String btn_yes , String btn_no , String image_title)
    {

        dialog.set_text(message,btn_yes,btn_no);
        dialog.set_image(image_title);

        yes = (Button) dialog.findViewById(R.id.alert_yes);
        no = (Button) dialog.findViewById(R.id.alert_no);

        txt = (TextView) dialog.findViewById(R.id.alert_msg);

        no.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)     {
                dialog_dismiss();
            }
        });
        dialog.show();
    }

    public void dialog_dismiss()
    {
        dialog.dismiss();
    }

    public void False_visible_btn_no()
    {
        no.setVisibility(View.INVISIBLE);
    }

}
