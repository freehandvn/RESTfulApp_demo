package com.app.restfulapp.reports;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restfulapp.R;
import com.app.restfulapp.models.Customer;
import com.app.restfulapp.models.Member;
import com.app.restfulapp.ultis.Parser;
import com.app.restfulapp.ultis.ReportLayout;
import com.app.restfulapp.ultis.Define;
import com.app.restfulapp.ultis.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by minhpham on 1/18/16.
 */
public class FrgSLTTReport extends FrgReport {

    private Member kind;

    @Override
    protected void initView() {
        super.initView();
        int unit = Utility.getMaxScreen(mActivity)/9;
//        reportLayout.setColumnWidth(new int[]{unit * 3, unit * 2, unit * 2, unit * 2});

        TextView txDate = (TextView) findViewById(R.id.txdate);

        txDate.setText(fromDate + " - " + toDate);

        if(kind == null) return;
        ((TextView) findViewById(R.id.tx_kind)).setText(kind.getName());

    }

    @Override
    protected int defineLayout() {
        return R.layout.frg_report_sltt;
    }

    @Override
    protected void requestData() {
        if(member == null){
            Toast.makeText(mActivity,"sale man not define",Toast.LENGTH_SHORT).show();
            return;
        }
        if(kind == null){
            Toast.makeText(mActivity,"part kind not define",Toast.LENGTH_SHORT).show();
            return;
        }
        mActivity.showLoading(true);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(mActivity.getCookieStore());
        ////sale_no: "6073", part_kind: "A", tc_date1: "2012-09-01", tc_date2: "2015-09-01"}
        client.get(String.format(Define.SLTT_URL, member.getCode(),kind.getCode(),
                        fromDate, toDate), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mActivity.showLoading(false);
                        Log.d("minh", response.toString());
                        //success request
                        if(Parser.isSuccess(response)){
                            try {
                                TextView txName = (TextView) findViewById(R.id.tx_name);
                                txName.setText(response.optJSONObject("Result").optString("sale_ename"));
                                reportLayout.setDataAndLayout(Parser.parseSLTT(response.optJSONObject("Result")));
                            }catch (ReportLayout.DataFormatException e){
                                e.printStackTrace();
                            }
                        }else{
                            // show error
                            Toast.makeText(mActivity, Parser.getError(response), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        mActivity.showLoading(false);
                        Toast.makeText(mActivity,responseString,Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public FrgReport setData(Member member,Member kind, String fromDate, String toDate) {
        super.setData(null, member, fromDate, toDate);
        this.kind = kind;
        return this;
    }
}
