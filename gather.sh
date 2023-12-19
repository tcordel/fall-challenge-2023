#!/bin/bash

folder_out='target'
folder='src/main/java/fr/tcordel';

rm -f ${folder_out}/outImport.java

# First manage imports
for classes in `find $folder -type f | grep -v NotPackaged`; do
    should_discard=`cat $classes | grep -c '@NotPackaged'`;
    if [ "$should_discard" == "0" ]; then
      cat $classes | grep "import" | grep -v "import fr.tcordel" | grep -v "import static">> ${folder_out}/outImport.java
    fi
done;

# sort and filter for duplicates
sort ${folder_out}/outImport.java | uniq > ${folder_out}/out.java
rm -f ${folder_out}/outImport.java


# concat code
for classes in `find $folder -type f | grep -v NotPackaged`; do
  should_discard=`cat $classes | grep -c '@NotPackaged'`;
  if [ "$should_discard" == "0" ]; then
    sed -e 's/public class/class/g' -e 's/public abstract class/abstract class/g' -e 's/public interface/interface/g' -e 's/public enum/enum/g' -e 's/int PRIME = true;//g' $classes | grep -v "package\|import" >> ${folder_out}/out.java
  fi
done;
echo "generated file location ! $(cd ${folder_out}; pwd)/out.java"