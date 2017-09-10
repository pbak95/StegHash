import { StegHashFrontendPage } from './app.po';

describe('steg-hash-frontend App', () => {
  let page: StegHashFrontendPage;

  beforeEach(() => {
    page = new StegHashFrontendPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
