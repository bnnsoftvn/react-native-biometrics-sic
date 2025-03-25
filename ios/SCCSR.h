
#import <Foundation/Foundation.h>

@interface SCCSR : NSObject

@property (nonatomic, strong) NSString* countryName;
@property (nonatomic, strong) NSString* organizationName;
@property (nonatomic, strong) NSString* organizationalUnitName;
@property (nonatomic, strong) NSString* commonName;
@property (nonatomic, strong) NSString* stateName;
@property (nonatomic, strong) NSString* localityName;

@property (nonatomic, strong) NSData* subjectDER;

-(NSData *) build:(NSData *)publicKeyBits privateKey:(SecKeyRef)privateKey;

@end