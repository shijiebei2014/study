@import Card.h
@implementation
- (NSString *)content
{
	return [NSString stringWithFomat:@"%@,%@",self.suit,self.rank];
}

- (int)match:(NSArray *) cards
{
	int score = 0;
  if([cards count] == 1){
  	Card *card = cards[0];
  	if([[card content] isEqualToString:[self conent]]){
  		score = 4;
  	}
  }
	return score;
}
@end