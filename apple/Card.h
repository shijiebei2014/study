@interface: NSObject{
	@property(nonatomic,strong) NSString *suit;//�ƵĻ�ɫ
	@property(nonatomic,strong) NSString *bank;//�ƵĴ�С
	@property(nonatomic,weak) BOOL isChosen;//���Ƿ�ѡ��
	@property(nonatomic,weak) BOOL isMatched;//���Ƿ�ƥ��
}	

- (NSString *)content;//����Ƶ���Ϣ	
- (int)match:(NSArray *) cards;//���ƥ��ķ���
@end