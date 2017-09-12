package comm.example.administrator.studentamagementsystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

/**
 * Created by 刺雒 on 2017/7/7.
 */

public class PermissionUtils {
    private Context mContext;
    private static PermissionUtils sPermissionUtils;

    public static PermissionUtils getInstance() {
        if(sPermissionUtils == null) {
            synchronized (PermissionUtils.class) {
                sPermissionUtils = new PermissionUtils();
            }
        }
        return sPermissionUtils;
    }

    public PermissionUtils build(final AppCompatActivity baseActivity, String... permission) {
        AndPermission.with(baseActivity)
                .permission(permission)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        Toast.makeText(baseActivity
                                ,baseActivity.getResources().getString(R.string.permission_warn),Toast.LENGTH_LONG).show();
                    }
                })
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(baseActivity,rationale).show();
                    }
                })
                .start();
        return sPermissionUtils;
    }

    public PermissionUtils build(final Fragment baseActivity, String... permission) {
        AndPermission.with(baseActivity)
                .permission(permission)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        Toast.makeText(baseActivity.getContext(),baseActivity.getResources().getString(R.string.permission_warn),Toast.LENGTH_LONG).show();

                    }
                })
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(baseActivity.getActivity(),rationale).show();
                    }
                })
                .start();
        return sPermissionUtils;
    }
}
