  //region Select Image
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.i(TAG, "onClick: "+item);
                if (items[item].equals("Take Photo")) {
                    selectCamera = true;
                    if (checkPermission()) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    } else {
                        requestPermission();
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    selectStorage = true;
                    if (checkPermission()) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select File"), 0);
                    } else {
                        requestPermission();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (BuildConfig.DEBUG) Log.e("requestCode", requestCode + " test");
        Calendar calendar = Calendar.getInstance();
        switch (requestCode) {
            case 0:

                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
                    cursor.close();
                    // Convert file path into bitmap image using below line.
                    yourSelectedImage = BitmapFactory.decodeFile(new File(filePath).getAbsolutePath());
                    imageViewOffer.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageViewOffer.setImageBitmap(yourSelectedImage);
                    strPhysicalImage = Constant.encodeToBase64(yourSelectedImage,Bitmap.CompressFormat.JPEG,100);
                    if (BuildConfig.DEBUG) Log.e("State", filePath + "_test");


                    imageName = "img-" + calendar.getTimeInMillis();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    if (requestCode == 1) {
                        imgurl = (Bitmap) data.getExtras().get("data");
                        imageViewOffer.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageViewOffer.setImageBitmap(imgurl);
                        imageName = "img-" + calendar.getTimeInMillis();
                            strPhysicalImage = Constant.encodeToBase64(imgurl, Bitmap.CompressFormat.JPEG, 100);
                        }
                    }
                break;
        }
    }
    //endregion

    //region Permission
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE}, 200);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (selectCamera) {
                        if (cameraAccepted) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(context, "App need camera permition to capture image.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (selectStorage) {
                        if (StorageAccepted) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), 0);
                        } else {
                            Toast.makeText(this, "App need Storage permission to receive image from file manager.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;
        }
    }
//endregion
