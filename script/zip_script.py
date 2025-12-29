import os
import sys
import zipfile

"""
For reasons unknown, calling `zip -rv $EXPORT_DIR"/mapping-$BUILD_ID.zip" $EXPORT_MAPPING_DIR`
from the bash script creates a zip is a valid size but can never be opened. Work around this
by using the python zipfile module instead.
"""
def main(export_dir, export_mapping_dir, build_id):
    zip_file_path = os.path.join(export_dir, f'mapping-{build_id}.zip')

    if not os.path.isdir(export_dir) or not os.path.isdir(export_mapping_dir):
        print("Error: Specified directories do not exist.")
        sys.exit(1)

    with zipfile.ZipFile(zip_file_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(export_mapping_dir):
            for file in files:
                file_path = os.path.join(root, file)
                arcname = os.path.relpath(file_path, export_mapping_dir)
                zipf.write(file_path, arcname)

    print(f"Zip file created successfully at {zip_file_path}")

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python zip_script.py [export_dir] [export_mapping_dir] [build_id]")
        sys.exit(1)
    main(sys.argv[1], sys.argv[2], sys.argv[3])
