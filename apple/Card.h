@interface: NSObject{
	@property(nonatomic,strong) NSString *suit;//牌的花色
	@property(nonatomic,strong) NSString *bank;//牌的大小
	@property(nonatomic,weak) BOOL isChosen;//牌是否被选中
	@property(nonatomic,weak) BOOL isMatched;//牌是否匹配
}	

- (NSString *)content;//获得牌的信息	
- (int)match:(NSArray *) cards;//获得匹配的分数
@end