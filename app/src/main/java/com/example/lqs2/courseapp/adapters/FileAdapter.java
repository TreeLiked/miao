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
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.PermissionUtils;

import java.io.IOException;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 文件适配器
 *
 * @author lqs2
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context mContext;
    private FileFragment fileFragment;
    private List<File> mFileList;

    public FileAdapter(Context context, FileFragment fileFragment) {
        this.mContext = context;
        this.fileFragment = fileFragment;
    }


    /**
     * 设置数据
     *
     * @param mFileList 数据
     */
    public void setData(List<File> mFileList) {
        this.mFileList = mFileList;
        notifyDataSetChanged();
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
                        .setTitle(file.getFileName())
                        .setMessage("大       小：" + file.getFileSize()
                                + "\n" + "编       号：" + file.getFileBringId()
                                + "\n" + "归       属：" + file.getFilePostAuthor()
                                + "\n" + "去       向：" + file.getFileDestination()
                                + "\n" + "备       注：" + file.getFileAttach()
                                + "\n" + "上传日期：" + file.getFilePostDate()
                                + "\n" + "有   效   期：" + file.getFileSaveDays() + "天")
                        .setNegativeButton("删除", v1 -> {
                            HttpUtil.deleteOneFile(FileFragment.un, String.valueOf(file.getId()), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    showToast("删除失败，请稍后重试", 0);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    assert response.body() != null;
                                    if ("1".equals(response.body().string())) {
                                        QServiceCfg qServiceCfg = QServiceCfg.instance(mContext);
                                        qServiceCfg.setUploadCosPath(file.getFileBucketId());
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
                            fileFragment.downloadFile(file.getFileBucketId(), file.getFileName());
                        })
                        .setCanceledOnTouchOutside(true);
                mMaterialDialog.show();
            } else {
                fileFragment.openFileByPath(mContext, Constant.DOWNLOAD_DIR + java.io.File.separator + file.getFileName());
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
                                fileFragment.deleteFileInDownloadDir(file.getFileName());
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
        holder.fileIcon.setImageBitmap(ImageTools.getImageFromAssetsFile(MyApplication.getContext(), "file_icons", getFileIconType(file.getFileName())));
        holder.fileName.setText(file.getFileName());
        holder.fileDate.setText(file.getFilePostDate().substring(0, file.getFilePostDate().lastIndexOf(":")));
        holder.fileSize.setText(file.getFileSize());

    }

    /**
     * 获取文件的图标类型
     *
     * @param filename 文件名
     * @return 文件前缀
     */
    private String getFileIconType(String filename) {
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        suffix = suffix.toLowerCase();
        suffix += ".png";
        return suffix;
    }

    @Override
    public int getItemCount() {
        return mFileList != null ? mFileList.size() : -1;
    }

    /**
     * 显示toast
     *
     * @param c 内容
     * @param t 时长
     */
    private void showToast(String c, int t) {
        fileFragment.showToast(c, t);
    }

    /**
     * 清空list
     */
    public void clear() {
        if (mFileList != null) {
            mFileList.removeAll(mFileList);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView fileIcon;
        TextView fileName;
        TextView fileDate;
        TextView fileSize;


        ViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view;
            fileIcon = view.findViewById(R.id.file_item_icon);
            fileName = view.findViewById(R.id.file_item_name);
            fileDate = view.findViewById(R.id.file_item_date);
            fileSize = view.findViewById(R.id.file_item_size);
        }
    }
}
