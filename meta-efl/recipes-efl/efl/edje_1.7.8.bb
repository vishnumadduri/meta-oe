DESCRIPTION = "Edje is the Enlightenment graphical design & layout library"
DEPENDS = "lua5.1 eet evas ecore embryo edje-native eina libsndfile1 eio"
DEPENDS_virtclass-native = "lua5.1-native evas-native ecore-native eet-native embryo-native eina-native"
DEPENDS_virtclass-nativesdk = "evas-native ecore-native eet-native embryo-native eina-native"
# GPLv2 because of epp in PN-utils
LICENSE = "MIT BSD GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c18cc221a14a84b033db27794dc36df8"

inherit efl

BBCLASSEXTEND = "native nativesdk"

do_configure_prepend_virtclass-native() {
    sed -i 's:EMBRYO_PREFIX"/bin:"${STAGING_BINDIR}:' ${S}/src/bin/edje_cc_out.c
    sed -i 's: cpp -I: /usr/bin/cpp -I:' ${S}/src/bin/edje_cc_parse.c
    sed -i 's:\"gcc -I:\"/usr/bin/gcc -I:' ${S}/src/bin/edje_cc_parse.c
}
# The new lua stuff is a bit broken...
do_configure_append() {
    for i in $(find "${S}" -name "Makefile") ; do
        sed -i -e 's:-L/usr/local/lib::g'  $i
    done
}

do_compile_append() {
    sed -i -e s:local/::g -e 's:-L${STAGING_LIBDIR}::g' ${S}/edje.pc
}

# gain some extra performance at the expense of RAM - generally i'd say bad
# and a possible source of bugs
#EXTRA_OECONF = "--enable-edje-program-cache"

# Since r44323 edje has a fixed-point mode
require edje-fpu.inc
EXTRA_OECONF += "${@get_edje_fpu_setting(bb, d)}"

SNDFILE = "--enable-sndfile"
SNDFILE_virtclass-native = "--disable-sndfile"
SNDFILE_virtclass-nativesdk = "--disable-sndfile"
EXTRA_OECONF += "${SNDFILE}"

PACKAGES =+ "${PN}-utils"
RDEPENDS_${PN}-utils = "cpp cpp-symlinks embryo-tests"

RRECOMMENDS_${PN}-utils = "\
    evas-saver-png \
    evas-saver-jpeg \
    evas-saver-eet \
"

DEBIAN_NOAUTONAME_${PN}-utils = "1"
# Some upgrade path tweaking
AUTO_LIBNAME_PKGS = ""

FILES_${PN}-utils = "\
    ${bindir}/edje_* \
    ${bindir}/inkscape2edc \
    ${libdir}/edje/utils/epp \
    ${datadir}/edje/include/edje.inc \
"

FILES_${PN} += "${libdir}/${PN}/modules/*/*/module.so \
                ${datadir}/mime/packages/edje.xml"
FILES_${PN}-dev += "${libdir}/${PN}/modules/*/*/module.la"
FILES_${PN}-dbg += "${libdir}/${PN}/modules/*/*/.debug"

SRC_URI = "\
    ${E_MIRROR}/${SRCNAME}-${SRCVER}.tar.gz \
"

SRC_URI[md5sum] = "06399e95de312de12342df8f16c7c885"
SRC_URI[sha256sum] = "924eae1be1fe755997be8074d32af4b6a83ed7e57057edccb2cf46b83c33202d"
