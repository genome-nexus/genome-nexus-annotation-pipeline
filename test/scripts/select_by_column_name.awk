#!/usr/bin/awk -f
# Process standard input outputting named columns provided as arguments.
#
# For example, given foo.dat containing
#     a b c c
#     1a 1b 1c 1C
#     2a 2b 2c 2C
#     3a 3b 3c 3C
# Running
#   cat foo.dat | ./namedcols c b a a d
# will output
#   1c 1b 1a 1a d
#   2c 2b 2a 2a d
#   3c 3b 3a 3a d
# and will warn on standard error that it
#   Ignored duplicate 'c' in column 4
# Notice that the requested but missing column d contains "d".
#
# Using awk's -F feature it is possible to parse comma-separated data:
#   cat foo.csv | ./namedcols -F, c b a a d
BEGIN {
    for (i=1; i<ARGC; ++i)
        desired[i] = ARGV[i]
    delete ARGV
}
NR==1 {
    for (i=1; i<=NF; i++)
        if ($i in names)
            printf "Ignored duplicate '%s' in column %d\n", $i, i | "cat 1>&2"
        else
            names[$i] = i
    next
}
{
    for (i=1; i<ARGC; ++i)
        printf "%s%s",                                          \
               (i==1 ? "" : OFS),                               \
               ((ndx = names[name = desired[i]])>0 ? $ndx: name)
    printf RS
}
