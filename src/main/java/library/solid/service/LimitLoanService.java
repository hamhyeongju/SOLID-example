package library.solid.service;

import library.solid.domain.*;
import library.solid.exception.OutOfLoanLimitException;
import library.solid.exception.OutOfStockException;
import library.solid.repository.BookRepository;
import library.solid.repository.LoanRepository;
import library.solid.repository.MemberRepository;

/**
 * BASIC - 최대 1권 대출 가능
 * VIP - 최대 3권 대출 가능
 *
 * 공통 정책 - 책 가격의 10% 요금
 */
public class LimitLoanService implements LoanService{

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LimitLoanService(MemberRepository memberRepository, BookRepository bookRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan loan(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId);
        Book book = bookRepository.findById(bookId);

        if (book.getStockQuantity() == 0) throw new OutOfStockException();
        if (member.getGrade().equals(Grade.BASIC) && member.getLoans().size() >= 1)
            throw new OutOfLoanLimitException();
        else if (member.getGrade().equals(Grade.VIP) && member.getLoans().size() >= 3)
            throw new OutOfLoanLimitException();

        return Loan.createLoan(Sequence.getSequence(), book.getPrice() / 10, member, book);
    }

    @Override
    public void returnBook(Loan loan) {
        loanRepository.delete(loan);
    }
}
