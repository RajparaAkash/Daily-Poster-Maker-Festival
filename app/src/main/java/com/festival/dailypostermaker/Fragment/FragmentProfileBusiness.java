package com.festival.dailypostermaker.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.AddBusinessDetails.AddBusiness;
import com.festival.dailypostermaker.Model.UpdateBusinessDetails.UpdateBusinessDetails;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.FragmentProfileBusinessBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfileBusiness extends Fragment {

    private String profilePath;
    private Dialog pleaseWaitDialog;
    private boolean isProfileChange = false;
    private int selectedMedia_B = 1;

    private FragmentProfileBusinessBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickLogo =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    startCropActivity(uri);
                }
            });

    public FragmentProfileBusiness() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBusinessBinding.inflate(inflater, container, false);

        pickLogo = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                startCropActivity(uri);
            }
        });

        setupClickListeners();

        if (MyPreference.get_IsBusinessAdded()) {

            binding.bEtName.setText(MyPreference.get_B_Name());
            binding.bEtDesignation.setText(MyPreference.get_B_Designation());
            binding.bEtAddress.setText(MyPreference.get_B_Address());
            binding.bEtWhatsapp.setText(MyPreference.get_B_Whatsapp());

            if (MyPreference.get_B_SelectedSocialMedia() == 1) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.instagram));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.instagram));
                binding.bEtSocialMedia.setText(MyPreference.get_B_Instagram());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_instagram);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_B_SelectedSocialMedia() == 2) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.youTube));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.youTube));
                binding.bEtSocialMedia.setText(MyPreference.get_B_YouTube());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_youtube);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_B_SelectedSocialMedia() == 3) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.facebook));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.facebook));
                binding.bEtSocialMedia.setText(MyPreference.get_B_Facebook());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_facebook);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_B_SelectedSocialMedia() == 4) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.twitter));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.twitter));
                binding.bEtSocialMedia.setText(MyPreference.get_B_Twitter());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_twitter);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_B_SelectedSocialMedia() == 5) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.email));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.email));
                binding.bEtSocialMedia.setText(MyPreference.get_B_Email());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_email);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_B_SelectedSocialMedia() == 6) {
                binding.bTxtSocialMedia.setText(getResources().getString(R.string.website));
                binding.bEtSocialMedia.setHint(getResources().getString(R.string.website));
                binding.bEtSocialMedia.setText(MyPreference.get_B_Website());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_website);
                binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
            }

            profilePath = MyPreference.get_B_ProfilePath();

            if (profilePath.isEmpty()) {
                binding.bProfileImg.setVisibility(View.GONE);
            } else {
                binding.bProfileImg.setVisibility(View.VISIBLE);
                Glide.with(requireContext()).load(profilePath)
                        .placeholder(R.drawable.img_place_holder)
                        .error(R.drawable.img_place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.bProfileImg);
            }
        }

        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.bLayChangeSocialMedia.setOnClickListener(view -> {
            dialogChangeSocialMedia();
        });

        binding.updateTxt.setOnClickListener(view -> {
            updateClick();
        });

        binding.bProfileLay.setOnClickListener(view -> {
            pickLogo.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void dialogChangeSocialMedia() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_social_media, null);
        sheetDialog.setContentView(bottomSheetView);
        sheetDialog.show();

        LinearLayoutCompat d_SM_Instagram_Lay = sheetDialog.findViewById(R.id.d_SM_Instagram_Lay);
        LinearLayoutCompat d_SM_Youtube_Lay = sheetDialog.findViewById(R.id.d_SM_Youtube_Lay);
        LinearLayoutCompat d_SM_Facebook_Lay = sheetDialog.findViewById(R.id.d_SM_Facebook_Lay);
        LinearLayoutCompat d_SM_Twitter_Lay = sheetDialog.findViewById(R.id.d_SM_Twitter_Lay);
        LinearLayoutCompat d_SM_Email_Lay = sheetDialog.findViewById(R.id.d_SM_Email_Lay);
        LinearLayoutCompat d_SM_Website_Lay = sheetDialog.findViewById(R.id.d_SM_Website_Lay);

        d_SM_Instagram_Lay.setOnClickListener(view -> {
            selectedMedia_B = 1;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.instagram));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.instagram));
            binding.bEtSocialMedia.setText(MyPreference.get_B_Instagram());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_instagram);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Youtube_Lay.setOnClickListener(view -> {
            selectedMedia_B = 2;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.youTube));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.youTube));
            binding.bEtSocialMedia.setText(MyPreference.get_B_YouTube());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_youtube);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Facebook_Lay.setOnClickListener(view -> {
            selectedMedia_B = 3;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.facebook));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.facebook));
            binding.bEtSocialMedia.setText(MyPreference.get_B_Facebook());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_facebook);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Twitter_Lay.setOnClickListener(view -> {
            selectedMedia_B = 4;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.twitter));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.twitter));
            binding.bEtSocialMedia.setText(MyPreference.get_B_Twitter());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_twitter);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Email_Lay.setOnClickListener(view -> {
            selectedMedia_B = 5;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.email));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.email));
            binding.bEtSocialMedia.setText(MyPreference.get_B_Email());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_email);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Website_Lay.setOnClickListener(view -> {
            selectedMedia_B = 6;
            sheetDialog.dismiss();
            binding.bTxtSocialMedia.setText(getResources().getString(R.string.website));
            binding.bEtSocialMedia.setHint(getResources().getString(R.string.website));
            binding.bEtSocialMedia.setText(MyPreference.get_B_Website());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_website);
            binding.bEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });
    }

    private void updateClick() {
        if (cheakValidation()) {
            showPleaseWaitDialog();

            MyPreference.set_B_SelectedSocialMedia(selectedMedia_B);

            ApiEndpoints apiEndpoints = RetrofitClient.getInstance().create(ApiEndpoints.class);

            if (MyPreference.get_B_SelectedSocialMedia() == 1) {
                MyPreference.set_B_Instagram(binding.bEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_B_SelectedSocialMedia() == 2) {
                MyPreference.set_B_YouTube(binding.bEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_B_SelectedSocialMedia() == 3) {
                MyPreference.set_B_Facebook(binding.bEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_B_SelectedSocialMedia() == 4) {
                MyPreference.set_B_Twitter(binding.bEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_B_SelectedSocialMedia() == 5) {
                MyPreference.set_B_Email(binding.bEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_B_SelectedSocialMedia() == 6) {
                MyPreference.set_B_Website(binding.bEtSocialMedia.getText().toString().trim());
            }

            RequestBody versionCode = RequestBody.create(String.valueOf(BuildConfig.VERSION_CODE), MediaType.parse("text/plain"));
            RequestBody deviceId = RequestBody.create(MyUtil.device_androidId, MediaType.parse("text/plain"));
            RequestBody name = RequestBody.create(binding.bEtName.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody whatsapp = RequestBody.create(binding.bEtWhatsapp.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody designation = RequestBody.create(binding.bEtDesignation.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody address = RequestBody.create(binding.bEtAddress.getText().toString().trim(), MediaType.parse("text/plain"));

            RequestBody instagram = RequestBody.create(MyPreference.get_B_Instagram(), MediaType.parse("text/plain"));
            RequestBody email = RequestBody.create(MyPreference.get_B_Email(), MediaType.parse("text/plain"));
            RequestBody youtube = RequestBody.create(MyPreference.get_B_YouTube(), MediaType.parse("text/plain"));
            RequestBody facebook = RequestBody.create(MyPreference.get_B_Facebook(), MediaType.parse("text/plain"));
            RequestBody twitter = RequestBody.create(MyPreference.get_B_Twitter(), MediaType.parse("text/plain"));
            RequestBody website = RequestBody.create(MyPreference.get_B_Website(), MediaType.parse("text/plain"));

            MultipartBody.Part logoImage = null;

            if (isProfileChange) {
                File file = new File(profilePath);
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
                logoImage = MultipartBody.Part.createFormData("logo", file.getName(), requestFile);
            }

            if (MyPreference.get_IsBusinessAdded()) {
                Call<UpdateBusinessDetails> call = apiEndpoints.updateBusinessProfile(
                        MyPreference.get_B_BusinessId(),
                        versionCode, deviceId, logoImage, name, email, designation, address, whatsapp, instagram, youtube, facebook, twitter, website);

                call.enqueue(new Callback<UpdateBusinessDetails>() {
                    @Override
                    public void onResponse(@NonNull Call<UpdateBusinessDetails> call, @NonNull Response<UpdateBusinessDetails> response) {
                        dismissPleaseWaitDialog();
                        if (response.isSuccessful()) {
                            if (response.body().getCode() == 2) {
                                saveBusinessDetails();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UpdateBusinessDetails> call, @NonNull Throwable t) {
                        dismissPleaseWaitDialog();
                        Log.d("TAG", "onFailure: " + t.getMessage());
                    }
                });

            } else {
                Call<AddBusiness> call = apiEndpoints.addBusinessProfile(
                        versionCode, deviceId, logoImage, name, email, designation, address, whatsapp, instagram, youtube, facebook, twitter, website);

                call.enqueue(new Callback<AddBusiness>() {
                    @Override
                    public void onResponse(@NonNull Call<AddBusiness> call, @NonNull Response<AddBusiness> response) {
                        dismissPleaseWaitDialog();
                        if (response.isSuccessful()) {
                            if (response.body().getCode() == 2) {
                                MyPreference.set_IsBusinessAdded(true);
                                MyPreference.set_B_BusinessId(response.body().getData().getBusiness().getId());
                                saveBusinessDetails();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddBusiness> call, @NonNull Throwable t) {
                        dismissPleaseWaitDialog();
                        Log.d("TAG", "onFailure: " + t.getMessage());
                    }
                });
            }
        }
    }


    private void saveBusinessDetails() {
        MyPreference.set_B_Name(binding.bEtName.getText().toString().trim());
        MyPreference.set_B_Designation(binding.bEtDesignation.getText().toString().trim());
        MyPreference.set_B_Address(binding.bEtAddress.getText().toString().trim());
        MyPreference.set_B_Whatsapp(binding.bEtWhatsapp.getText().toString());

        MyPreference.set_B_ProfilePath(profilePath);

        if (!MyPreference.get_IsProfileBusiness()) {
            MyUtil.isAlternative = true;
            MyUtil.isUpdate = false;
            MyPreference.set_IsProfileBusiness(true);
        } else {
            MyUtil.isAlternative = false;
            MyUtil.isUpdate = true;
        }

        Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    private boolean cheakValidation() {

        if (TextUtils.isEmpty(binding.bEtName.getText().toString().trim())) {
            binding.bEtName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.bEtDesignation.getText().toString().trim())) {
            binding.bEtDesignation.requestFocus();
            return false;
        }
        if (!MyPreference.get_IsBusinessAdded()) {
            if (TextUtils.isEmpty(profilePath)) {
                Toast.makeText(requireContext(), "Please Add Business Logo", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (TextUtils.isEmpty(binding.bEtAddress.getText().toString().trim())) {
            binding.bEtAddress.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.bEtWhatsapp.getText().toString().trim())) {
            binding.bEtWhatsapp.requestFocus();
            return false;
        }
        if (binding.bEtWhatsapp.getText().toString().trim().length() < 10) {
            binding.bEtWhatsapp.setError("Enter Valid Whatsapp No");
            binding.bEtWhatsapp.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedImageUri = UCrop.getOutput(data);
            handleCrop(MyUtil.getPathFromURI(requireContext(), croppedImageUri));
        }
    }

    private void startCropActivity(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        UCrop.of(imageUri, Uri.fromFile(new File(requireContext().getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png")))
                .withAspectRatio(1, 1).withOptions(options).start(requireContext(), this);
    }

    private void handleCrop(String imageFilePath) {
        try {
            File newfile = new File(MyUtil.getAppFolder(requireContext()) + "profile");
            if (!newfile.exists()) {
                newfile.mkdir();
                newfile.mkdirs();
            }
            MyUtil.cropFileMove(requireContext(), new File(imageFilePath), newfile);

            profilePath = newfile.getAbsolutePath() + "/" + new File(imageFilePath).getName();
            Glide.with(requireContext()).load(profilePath).placeholder(R.drawable.img_place_holder).error(R.drawable.img_place_holder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.bProfileImg);

            binding.bProfileImg.setVisibility(View.VISIBLE);
            isProfileChange = true;
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showPleaseWaitDialog() {
        try {
            pleaseWaitDialog = new Dialog(requireActivity());
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);

            if (!requireActivity().isFinishing() && !requireActivity().isDestroyed() && pleaseWaitDialog != null) {
                pleaseWaitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!requireActivity().isFinishing() && !requireActivity().isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
        }
    }
}