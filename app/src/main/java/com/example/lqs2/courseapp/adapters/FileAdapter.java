package com.example.lqs2.courseapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lqs2.courseapp.MyApplication;
import com.example.lqs2.courseapp.ObjectSample.DeleteObjectSample;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.common.QServiceCfg;
import com.example.lqs2.courseapp.entity.File;
import com.example.lqs2.courseapp.fragment.FileFragment;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.HttpUtil;
import com.example.lqs2.courseapp.utils.PermissionUtils;
import com.example.lqs2.courseapp.utils.Tools;

import java.io.IOException;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context mContext;
    private FileFragment fileFragment;
    private List<File> mFileList;

    public FileAdapter(Context context, FileFragment fileFragment) {
        this.mContext = context;
        this.fileFragment = fileFragment;
    }


    public void setData(List<File> mFileList) {
        this.mFileList = mFileList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView file_icon;
        TextView file_name;
        TextView file_date;
        TextView file_size;


        ViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view;
            file_icon = view.findViewById(R.id.file_item_icon);
            file_name = view.findViewById(R.id.file_item_name);
            file_date = view.findViewById(R.id.file_item_date);
            file_size = view.findViewById(R.id.file_item_size);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.file_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.linearLayout.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            File file = mFileList.get(position);
            if (FileFragment.isCloud) {
                final MaterialDialog mMaterialDialog = new MaterialDialog(mContext);
                mMaterialDialog
                        .setTitle(file.getFile_name())
                        .setMessage("大       小：" + file.getFile_size()
                                + "\n" + "编       号：" + file.getFile_bring_id()
                                + "\n" + "归       属：" + file.getFile_post_author()
                                + "\n" + "去       向：" + file.getFile_destination()
                                + "\n" + "备       注：" + file.getFile_attach()
                                + "\n" + "上传日期：" + file.getFile_post_date()
                                + "\n" + "有   效   期：" + file.getFile_save_days() + "天")
                        .setNegativeButton("删除", v1 -> {
                            HttpUtil.deleteOneFile(FileFragment.un, String.valueOf(file.getId()), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    showToast("删除失败，请稍后重试", 0);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if ("1".equals(response.body().string())) {
                                        QServiceCfg qServiceCfg = QServiceCfg.instance(mContext);
                                        qServiceCfg.setUploadCosPath(file.getFile_bucket_id());
                                        DeleteObjectSample deleteObjectSample = new DeleteObjectSample(qServiceCfg, fileFragment);
                                        deleteObjectSample.startAsync();
                                    } else {
                                        showToast("删除失败，呜～", 0);
                                    }
                                }
                            });
                            mMaterialDialog.dismiss();
                        })
                        .setPositiveButton("下载", v1 -> {
                            mMaterialDialog.dismiss();
                            fileFragment.downloadFile(file.getFile_bucket_id(), file.getFile_name());
                        })
                        .setCanceledOnTouchOutside(true);
                mMaterialDialog.show();
            } else {
                fileFragment.openFileByPath(mContext, Constant.DOWNLOAD_DIR + java.io.File.separator + file.getFile_name());
            }
        });
        if (!FileFragment.isCloud) {
            holder.linearLayout.setOnLongClickListener(v -> {
                if (PermissionUtils.checkWriteExtraStoragePermission(mContext)) {

                    int position = holder.getAdapterPosition();
                    File file = mFileList.get(position);
                    MaterialDialog dialog = new MaterialDialog(mContext);
                    dialog
                            .setTitle("删除此文件")
                            .setMessage("这将一并在本地移除文件")
                            .setPositiveButton("取消", v1 -> dialog.dismiss())
                            .setNegativeButton("确认删除", v1 -> {
                                fileFragment.deleteFileInDownloadDir(file.getFile_name());
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    PermissionUtils.requestWritePermission(mContext, fileFragment.getActivity(), PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
                }
                return true;
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        File file = mFileList.get(position);
        holder.file_icon.setImageBitmap(Tools.getImageFromAssetsFile(MyApplication.getContext(), "file_icons", getFileIconType(file.getFile_name())));
        holder.file_name.setText(file.getFile_name());
        holder.file_date.setText(file.getFile_post_date().substring(0, file.getFile_post_date().lastIndexOf(":")));
        holder.file_size.setText(file.getFile_size());

    }

    @Override
    public int getItemCount() {
        return mFileList != null ? mFileList.size() : -1;
    }

    private String getFileIconType(String filename) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        suffix = suffix.toLowerCase();
        suffix += ".png";
        return suffix;
    }

    private void showToast(String c, int t) {
        fileFragment.showToast(c, t);
    }

    public void clear() {
        if (mFileList != null) {
            mFileList.removeAll(mFileList);
        }
    }
}
