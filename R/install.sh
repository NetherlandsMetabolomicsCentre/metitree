R --slave <<EOF
source("http://bioconductor.org/biocLite.R")
biocLite("xcms")
biocLite("Rgraphviz")
biocLite("brew")
biocLite("RCurl")
biocLite("grid")
biocLite("bitops")
biocLite("graph")
EOF
