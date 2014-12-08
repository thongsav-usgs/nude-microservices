nude-microservices
==================

NUDE Framework functions abstracted into microservices. This is currently only a proof-of-concept.

Some assumptions/simplification used for proof:
1) All datasources are in CSV format.
2) No validation is being performed.
3) Request format is GET. URL chaining currently performed using escaped query parameters.

EXAMPLE USAGE:

Example data source URLs (from the nude-ms-rename module):
http://localhost:8080/nude-ms-rename/service/example/data/set1
http://localhost:8080/nude-ms-rename/service/example/data/set2
http://localhost:8080/nude-ms-rename/service/example/data/set3

Example usage of applying the rename transformation (transforming the example data sets above):
http://localhost:8080/nude-ms-rename/service/rename?sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Fexample%2Fdata%2Fset1&rename=ID,MUXABLE_KEY&rename=SET1_COLUMN1,RENAMED_S1_C1&rename=SET1_COLUMN2,RENAMED_S1_C2
http://localhost:8080/nude-ms-rename/service/rename?sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Fexample%2Fdata%2Fset2&rename=IDENTIFIER,MUXABLE_KEY&rename=SET2_COLUMN1,RENAMED_S2_C1&rename=SET2_COLUMN2,RENAMED_S2_C2
http://localhost:8080/nude-ms-rename/service/rename?sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Fexample%2Fdata%2Fset3&rename=KEY,MUXABLE_KEY&rename=SET3_COLUMN1,RENAMED_S3_C1&rename=SET3_COLUMN2,RENAMED_S3_C2

Example usage of apply 1 final MUX after 3 separate rename transforms (a composition of 4 transforms):
http://localhost:8080/nude-ms-mux/service/mux?sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Frename%3FsourceUrl%3Dhttp%253A%252F%252Flocalhost%253A8080%252Fnude-ms-rename%252Fservice%252Fexample%252Fdata%252Fset1%26rename%3DID%2CMUXABLE_KEY%26rename%3DSET1_COLUMN1%2CRENAMED_S1_C1%26rename%3DSET1_COLUMN2%2CRENAMED_S1_C2&sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Frename%3FsourceUrl%3Dhttp%253A%252F%252Flocalhost%253A8080%252Fnude-ms-rename%252Fservice%252Fexample%252Fdata%252Fset2%26rename%3DIDENTIFIER%2CMUXABLE_KEY%26rename%3DSET2_COLUMN1%2CRENAMED_S2_C1%26rename%3DSET2_COLUMN2%2CRENAMED_S2_C2&sourceUrl=http%3A%2F%2Flocalhost%3A8080%2Fnude-ms-rename%2Fservice%2Frename%3FsourceUrl%3Dhttp%253A%252F%252Flocalhost%253A8080%252Fnude-ms-rename%252Fservice%252Fexample%252Fdata%252Fset3%26rename%3DKEY%2CMUXABLE_KEY%26rename%3DSET3_COLUMN1%2CRENAMED_S3_C1%26rename%3DSET3_COLUMN2%2CRENAMED_S3_C2

TODO: Assess network overhead from chaining network calls.
